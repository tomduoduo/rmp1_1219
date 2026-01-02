# RowMaster Pro 开发规范

## 定时任务管理规范

### 1. TimerManager API 使用规范

**错误描述**：错误使用 `TimerManager.scheduleOnMainThread()` 方法参数，导致编译错误

**错误原因**：`scheduleOnMainThread()` 只接受3个参数，但代码中传入了4个参数（试图用于周期性调度）

**正确实现**：
- **一次性主线程任务**：使用 `scheduleOnMainThread(taskId, runnable, delay)`
- **周期性任务**：使用 `scheduleAtFixedRate(taskId, runnable, initialDelay, period)`
- **一次性延迟任务**：使用 `schedule(taskId, runnable, delay)`

**预防措施**：
- 使用IDE的自动补全功能查看方法签名
- 参考 TimerManager 类的 JavaDoc 注释
- 在调用前确认任务类型（一次性 vs 周期性）

**示例代码**：
```java
// ❌ 错误 - 试图用3参数方法做周期性调度
TimerManager.getInstance().scheduleOnMainThread("updateTask", () -> {
    updateUI();
}, 0, 1000);  // 编译错误：参数数量不匹配

// ✅ 正确 - 周期性任务使用 scheduleAtFixedRate
TimerManager.getInstance().scheduleAtFixedRate("updateTask", new Runnable() {
    @Override
    public void run() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUI(); // UI更新在主线程执行
            }
        });
    }
}, 0, 1000);
```

### 2. UI更新线程安全规范

**错误描述**：在后台线程中直接更新UI组件，可能导致应用崩溃

**错误原因**：Android UI组件不是线程安全的，必须在主线程中更新

**正确实现**：
- 所有UI更新操作必须通过 `runOnUiThread()` 包装
- 在TimerManager的Runnable内部使用嵌套的 `runOnUiThread()`
- 或者使用 `Handler` 和 `Looper.getMainLooper()`

**预防措施**：
- 建立代码审查checklist，重点检查定时任务中的UI更新代码
- 使用Android Lint工具检查线程安全问题
- 在代码注释中明确标记UI更新操作

**示例代码**：
```java
// ❌ 错误 - 在定时器线程中直接更新UI
TimerManager.getInstance().scheduleAtFixedRate("updateUI", new Runnable() {
    @Override
    public void run() {
        textView.setText("Updated"); // 可能崩溃！
    }
}, 0, 1000);

// ✅ 正确 - 使用 runOnUiThread 包装UI更新
TimerManager.getInstance().scheduleAtFixedRate("updateUI", new Runnable() {
    @Override
    public void run() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText("Updated"); // 安全在主线程执行
            }
        });
    }
}, 0, 1000);
```

### 3. 代码结构完整性规范

**错误描述**：类定义结构不完整，出现多余或缺失的闭合大括号

**错误原因**：代码重构过程中错误地添加或删除了大括号，导致类结构破坏

**正确实现**：
- 每个类、方法、控制结构都必须有正确匹配的大括号
- 使用一致的代码缩进（4个空格）
- 在重构后使用IDE的代码格式化功能

**预防措施**：
- 重构后立即使用IDE检查语法错误
- 使用版本控制系统，重构前创建备份分支
- 配置IDE显示行号和括号匹配高亮
- 定期进行代码格式化（Ctrl+Alt+L in Android Studio）

**示例代码**：
```java
// ❌ 错误 - 多余的大括号导致类提前结束
public class Dashboard extends Activity {
    private void method1() {
        // ... 代码 ...
    } // 正确结束
    } // ❌ 多余的大括号 - 类在这里意外结束！
    
    private void method2() { // ❌ 编译错误：方法在类外定义
        // ... 代码 ...
    }
}

// ✅ 正确 - 结构完整
public class Dashboard extends Activity {
    private void method1() {
        // ... 代码 ...
    } // 正确结束
    
    private void method2() { // ✅ 在类内部正确定义
        // ... 代码 ...
    }
} // 类正确结束
```

### 4. 定时任务生命周期管理规范

**错误描述**：定时任务没有被正确清理，导致内存泄漏

**错误原因**：Activity/Fragment销毁时未取消定时任务，持有对UI组件的引用

**正确实现**：
- 在 `onDestroy()` 或 `onPause()` 中取消所有定时任务
- 使用有意义的任务ID，便于管理和取消
- 建立任务ID命名规范（如：`activityName_taskName`）

**预防措施**：
- 在Activity/Fragment生命周期方法中添加任务清理代码
- 使用try-finally确保任务被取消
- 建立任务管理checklist

**示例代码**：
```java
public class MainActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 启动定时任务
        TimerManager.getInstance().scheduleAtFixedRate("mainUI_update", 
            updateRunnable, 0, 1000);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ✅ 正确 - 清理定时任务
        TimerManager.getInstance().cancelTask("mainUI_update");
    }
}
```

### 5. 代码审查重点检查项

**定时任务相关**：
- [ ] 确认使用正确的TimerManager方法（scheduleOnMainThread vs scheduleAtFixedRate）
- [ ] 检查所有UI更新是否通过runOnUiThread包装
- [ ] 验证任务ID命名是否清晰且唯一
- [ ] 确认生命周期方法中取消了所有定时任务
- [ ] 检查大括号匹配和代码结构完整性
- [ ] 验证线程安全性（特别是UI操作）

**通用检查项**：
- [ ] 代码格式一致性（缩进、括号、空格）
- [ ] 导入语句是否必要且正确
- [ ] 方法参数验证和异常处理
- [ ] 资源释放（文件、网络连接等）

## 总结

这些规范基于实际开发中出现的错误总结而成，重点关注：
1. **API正确使用** - 理解每个TimerManager方法的适用场景
2. **线程安全** - 确保UI操作在主线程执行
3. **代码结构** - 保持代码结构完整性和可读性
4. **资源管理** - 及时清理定时任务避免内存泄漏

遵循这些规范可以有效避免类似的编译错误和运行时问题，提高代码质量和可维护性。