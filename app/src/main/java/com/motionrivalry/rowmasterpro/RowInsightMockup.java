package com.motionrivalry.rowmasterpro;

public class RowInsightMockup {

    //------------------------------------------------------------------------------
    // EXTERNAL (GLOBAL) CACHE TO STORE RESULT
    private double strokeRateGlobal = 0;
    private double strokeCount = 0;
    private double boatSpin = 0;

    //------------------------------------------------------------------------------
    // INPUT FROM DEVICE AND SENSORS
    private double linearAccel_x = 0;
    private double linearAccel_y = 0;
    private double linearAccel_z = 0;
    private double giro_x = 0;
    private double giro_y = 0;
    private double giro_z = 0;
    private double currentSystemTime = 0;
    // in form of standard timestamps (ms);
    private double speedGPS = 0;
    // unit to be m/s

    //------------------------------------------------------------------------------
    // INITIATION OF KEY PARAMS FOR ALGORITHM
    private double outputRateIMU = 52;
    private double accelTranslationRatio = 0.244;
    // works on FS 8g mg/lsb config for IMU
    private double angularAccelTranslationRatio = 17.5;
    // works on FS 500 mdps/lsb config for IMU
    private double maxSPM = 50;
    // max stroke per minute (60000 ms)
    private double minStrokeGap = 60000/maxSPM;
    // minimal stroke gap
    private double maxIdleTime = 10000;
    // time to reset to 0 spm
    private double minBoatAccel = 1.2;
    // m/s at the moment
    // need to recalibrated to g/1000 for field implementation
    private double minBoatSpeed = 1.5;
    // m/s at the moment
    // min boat speed to reset stroke rate
    private double lowPassAlpha = 0.95;
    // alpha value for low pass filter to isolate gravity
    private int strokeRefreshLock = 0;
    private double strokeRefreshLowerThresh = -minBoatAccel * 0.8;
    private double accelBooster = 1.2;
    private double spinBooster = 1.3;

    //------------------------------------------------------------------------------
    // EXTERNAL (GLOBAL) CACHE VALUE
    private double strokeTimeLast = 0;
    private double gravity_x_cache = 0;
    private double gravity_y_cache = 0;
    private double gravity_z_cache = 0;

    //------------------------------------------------------------------------------
    // CREATION OF GLOBAL CACHE SPACES FOR MOVING AVERAGE CALCULATIONS
    // cache for moving average calculation of BOAT ACCELERATION
    private int acclCacheLength = 10;
    // max window length
    private double[] acclCacheSamples = new double[acclCacheLength];
    // creation of cache space
    private int acclCachePointer = 0;
    // pointer for add new data
    private int acclCacheSize = 0;
    // temporary storage of current cache length
    private double acclCacheSum = 0.0;
    // intermedia value (unused)

    // cache for moving average calculation of BOAT YAW
    private int boatYawCacheLength = 52;
    private double[] boatYawCacheSamples = new double[boatYawCacheLength];
    private int boatYawCachePointer = 0;
    private int boatYawCacheSize = 0;
    private double boatYawCacheSum = 0.0;

    // cache for moving average calculation of STROKE RATE
    private int SRCacheLength = 4;
    private double[] SRCacheSamples = new double[SRCacheLength];
    private int SRCachePointer = 0;
    private int SRCacheSize = 0;
    private double SRCacheSum = 0.0;

    //------------------------------------------------------------------------------
    // SPM Algorithm
    private void getRowData (double accel_x, double accel_y, double accel_z,
                             double gyro_x, double gyro_y, double gyro_z,
                             double timeNow, double speed){

        double strokeTimeNow = timeNow;
        double strokeGap = strokeTimeNow - strokeTimeLast;

        double accel_x_converted = accel_x * accelTranslationRatio / 1000 * 9.85;
        double accel_y_converted = accel_y * accelTranslationRatio / 1000 * 9.85;
        double accel_z_converted = accel_z * accelTranslationRatio / 1000 * 9.85;

        double gyro_x_converted = gyro_x * angularAccelTranslationRatio / 1000;
        double gyro_y_converted = gyro_y * angularAccelTranslationRatio / 1000;
        double gyro_z_converted = gyro_z * angularAccelTranslationRatio / 1000;

        double gravity_x = lowPassAlpha * gravity_x_cache + (1 - lowPassAlpha) * accel_x_converted;
        double gravity_y = lowPassAlpha * gravity_y_cache + (1 - lowPassAlpha) * accel_y_converted;
        double gravity_z = lowPassAlpha * gravity_z_cache + (1 - lowPassAlpha) * accel_z_converted;

        double accel_x_free = accel_x_converted - gravity_x;
        double accel_y_free = accel_y_converted - gravity_y;
        double accel_z_free = accel_z_converted - gravity_z;

        gravity_x_cache = gravity_x;
        gravity_y_cache = gravity_y;
        gravity_z_cache = gravity_z;

        double accelBoatNow = -accel_y_free + accel_z_free;
        double spinBoatNow = gyro_z_converted;

        if (acclCacheSize < acclCacheLength) {
            acclCacheSamples[acclCachePointer++] = accelBoatNow;
            acclCacheSize ++;

        }else{

            acclCachePointer = acclCachePointer % acclCacheLength;
            acclCacheSum -= acclCacheSamples[acclCachePointer];
            acclCacheSamples[acclCachePointer++] = accelBoatNow;
        }

        double accelBoatMA = doubleArrAverage(acclCacheSamples) * accelBooster;

        if (strokeGap > maxIdleTime && speed < minBoatSpeed ){
            strokeRateGlobal = 0;
            // export to GLOBAL CACHE as OUTPUT

            boatSpin = 0;
            // export to GLOBAL CACHE as OUTPUT

        }

        if(accelBoatMA > minBoatAccel){

            if (boatYawCacheSize < boatYawCacheLength) {
                boatYawCacheSamples[boatYawCachePointer++] = spinBoatNow;
                boatYawCacheSize ++;

            }else{
                boatYawCachePointer = boatYawCachePointer % boatYawCacheLength;
                boatYawCacheSum -= boatYawCacheSamples[boatYawCachePointer];
                boatYawCacheSamples[boatYawCachePointer++] = spinBoatNow;
            }

            boatSpin = doubleArrAverage(boatYawCacheSamples) * spinBooster;
            // export to GLOBAL CACHE as OUTPUT

            if (strokeGap > minStrokeGap && strokeRefreshLock == 0){

                strokeTimeLast = strokeTimeNow;
                double strokeRateNow;

                if (strokeCount < 2){
                    strokeRateNow = 0;
                }else{
                    strokeRateNow = 60000/strokeGap;

                }

                if (SRCacheSize < SRCacheLength) {

                    SRCacheSamples[SRCachePointer++] = strokeRateNow;
                    SRCacheSize ++;

                }else{

                    SRCachePointer = SRCachePointer % SRCacheLength;
                    SRCacheSum -= SRCacheSamples[SRCachePointer];
                    SRCacheSamples[SRCachePointer++] = strokeRateNow;

                }

                double strokeRateMA = doubleArrAverage(SRCacheSamples);

                strokeRateGlobal = strokeRateMA;
                // export to GLOBAL CACHE as OUTPUT

                strokeCount = strokeCount + 1;
                // export to GLOBAL CACHE as OUTPUT
                strokeRefreshLock = 1;

            }
        }else if (accelBoatMA <= strokeRefreshLowerThresh){

            strokeRefreshLock = 0;

            boatYawCacheSamples = new double[boatYawCacheLength];
            boatYawCachePointer = 0;
            boatYawCacheSize = 0;
            boatYawCacheSum = 0.0;

        }
    }

    private double doubleArrAverage(double[] arr) {
        double sum = 0;
        for(int i = 0;i < arr.length; i++) {
            sum += arr[i];
        }
        return sum / arr.length;
    }

}
