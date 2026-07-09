# Carp

> 生电从此进入歪瓜时代

Carp 是一个 Fabric 客户端模组，集成了精简移植版 baritone 寻路引擎，提供搭路、自动攻击、飞行、加速、除草等 15+ 功能，通过 MaLiLib 六标签 GUI 配置。

| 项 | 值 |
|----|-----|
| 最后更新 | 2026-07-09 |
| 版本 | **2.10.2** |
| MC | 26.2 (主) / 26.1.2 / 1.21.8 / 1.21.11 |
| JDK | 25 (26.x) / 21 (1.21.x) |
| MaLiLib | 0.29.2 (26.2) / 0.28.6 (26.1.2) / 0.25.7 (1.21.8) |
| baritone | API 149 文件 + 实现 67 文件（精简移植版） |

---

## 功能列表

| 标签 | 功能 |
|------|------|
| **建筑** | Scaffold（搭路/延迟/疾跑/槽位+快捷键）+ 除草 |
| **移动** | BoatFly / Elytra / Speed / 自动疾跑重生 / NoFall / NoSlow / NoWeb |
| **渲染** | 路径渲染 / 去南瓜 / 夜视 / 反恶心 / 反黑暗 |
| **玩家** | KillAura（范围/CPS/自动格挡+快捷键）+ 自动进食 |
| **自动** | GoTo / 挖矿 / 跟随 / 探索 / 隧道 + 寻路参数配置 |

---

## 支持的 MC 版本

| 版本 | MC 版本 | Fabric Loader | Fabric API | MaLiLib | Mod Menu | JDK |
|------|---------|---------------|------------|---------|----------|-----|
| **2.10.2** | 26.2 | 0.19.3 | 0.153.0+26.2 | 0.29.2 | 20.0.0-beta.4 | 25 |
| **2.10.2+mc26.1.2** | 26.1.2 | 0.18.4 | 0.147.0+26.1.2 | 0.28.6 | 18.0.0-beta.1 | 25 |
| **2.10.2+mc1.21.8** | 1.21.8 | 0.15.11 | 0.136.1+1.21.8 | 0.25.7 | 15.0.2 | 21 |
| **2.10.2+mc1.21.11** | 1.21.11 | 0.18.4 | 0.141.4+1.21.11 | 0.27.16 | 17.0.0 | 21 |

---

## 编译

### 环境要求

- **JDK 25** (MC 26.x 版本) 或 **JDK 21** (MC 1.21.x 版本)
- JDK 路径示例：`D:\software\java\java25` 或 `D:\software\java\java21`
- Gradle 会自动下载，无需手动安装

### 编译步骤

```bash
# 1. 克隆仓库
git clone https://github.com/your-username/carp.git
cd carp

# 2. 设置 JAVA_HOME（以 MC 26.2 版本为例）
# PowerShell
$env:JAVA_HOME = 'D:\software\java\java25'

# 或 CMD
set JAVA_HOME=D:\software\java\java25

# 3. 编译
.\gradlew.bat build --no-daemon

# 4. 产物位于
# build/libs/carp-2.10.2.jar
```

### 编译其他版本

```bash
# MC 26.1.2
cd 历史版本/2.10.2-mc26.1.2
$env:JAVA_HOME = 'D:\software\java\java25'
.\gradlew.bat build --no-daemon

# MC 1.21.8
cd 历史版本/2.10.2-mc1.21.8
$env:JAVA_HOME = 'D:\software\java\java21'
.\gradlew.bat build --no-daemon

# MC 1.21.11
cd 历史版本/2.10.2-mc1.21.11
$env:JAVA_HOME = 'D:\software\java\java21'
.\gradlew.bat build --no-daemon
```

### 使用 build.bat（可选）

```cmd
build.bat          # 构建并归档
build.bat clean    # 清理后构建
```

---

## 安装

1. 安装 [Fabric Loader](https://fabricmc.net/use/installer/)
2. 下载对应 MC 版本的 Fabric API
3. 下载 [MaLiLib](https://modrinth.com/mod/malilib)
4. 将编译产物 `carp-*.jar` 复制到 `.minecraft/mods/`
5. 启动游戏，按 **右Shift** 打开配置面板

---

## 项目结构

```
src/
├── api/java/baritone/          # baritone API 层 (149 文件)
│   └── api/
│       ├── Settings.java       # 所有 baritone 设置项
│       ├── BaritoneAPI.java    # 入口
│       ├── event/events/       # 事件 (TickEvent/RenderEvent)
│       ├── pathing/goals/      # GoalBlock/GoalXZ
│       └── process/            # ICustomGoalProcess/IFollowProcess
│
├── main/java/baritone/         # baritone 实现层 (67 文件)
│   ├── Baritone.java           # 核心实例
│   ├── behavior/
│   │   ├── PathingBehavior.java # A* 调度核心
│   │   └── LookBehavior.java   # 视角控制
│   ├── pathing/
│   │   ├── calc/AStarPathFinder.java
│   │   ├── movement/           # MovementTraverse/Diagonal/Ascend/...
│   │   └── path/PathExecutor.java
│   ├── process/                # CustomGoalProcess/FollowProcess/ExploreProcess
│   ├── cache/                  # CachedChunk/CachedRegion/WorldProvider
│   └── utils/                  # InputOverrideHandler/PathRenderer
│
├── main/java/com/example/carp/ # Carp 主项目
│   ├── CarpMod.java            # 入口 (IKeybindProvider)
│   ├── config/
│   │   ├── Config.java         # 所有配置项 (114 个)
│   │   ├── ConfigGui.java      # 六标签 GUI
│   │   └── CarpModMenuApi.java # Mod Menu 集成
│   ├── features/
│   │   ├── BaritoneProcess.java  # ★ 核心调度器
│   │   ├── Scaffold.java / KillAura.java / Speed.java
│   │   ├── BoatFly.java / FreeElytra.java / AutoEat.java
│   │   ├── Weeding.java / RenderFeatures.java
│   │   └── NoFall.java / NoSlow.java / NoWeb.java
│   └── mixin/
│       ├── BoatMixin.java / NoSlowMixin.java
│       ├── LevelRendererMixin.java
│       └── MixinPlugin.java
│
└── main/resources/
    ├── carp.mixins.json
    ├── fabric.mod.json
    └── assets/carp/lang/
        ├── zh_cn.json / en_us.json
```

---

## 技术架构

### baritone 寻路引擎

```
用户 → CarpMod → BaritoneProcess → BaritoneAPI → Baritone 实例
                                                    ├── PathingBehavior (A* 调度)
                                                    ├── LookBehavior (视角)
                                                    ├── PathExecutor (执行)
                                                    ├── CustomGoalProcess (GoTo)
                                                    ├── FollowProcess (跟随)
                                                    ├── ExploreProcess (探索)
                                                    └── GameEventHandler (事件分发)
```

### A* 寻路流程

```
PathingBehavior.secretInternalSetGoalAndPath(cmd)
  → CalculationContext (assumeWalkOnWater/allowBreak 等)
  → AStarPathFinder(start, goal, context)  [后台线程]
  → PathExecutor(current) 开始执行
  → Movement 链逐节点移动
  → InputOverrideHandler → player.input.keyPresses
```

---

## 开发

### 添加新功能

1. 在 `Config.java` 添加配置字段
2. 在 `features/Xxx.java` 写 `onClientTick(Minecraft)`
3. 在 `CarpMod.java` 实例化 + 注册 tick + 热键
4. 在 `ConfigGui.java` 加入对应标签页列表
5. 在 `zh_cn.json` / `en_us.json` 添加翻译
6. 编译：`.\gradlew.bat build`

### MaLiLib 配置类型

```java
// 热键
new ConfigHotkey("carp.xxx.hotkey", "RIGHT_SHIFT", "carp.xxx.hotkey.comment");
// 布尔开关
new ConfigBoolean("carp.xxx.enabled", false, "carp.xxx.enabled.comment");
// 整数滑块
new ConfigInteger("carp.xxx.delay", 100, 50, 300, "");
// 浮点滑块
new ConfigDouble("carp.xxx.range", 3.5, 1.0, 6.0, "carp.xxx.range.comment");
// 下拉选项
new ConfigOptionList("carp.xxx.mode", XxxMode.GROUND, "carp.xxx.mode.comment");
```

---

## 已知问题

- [ ] KillAura 不支持瞄准旋转
- [ ] Scaffold GodBridge 侧面放置精度待优化

---

## 致谢

- [Baritone](https://github.com/cabaletta/baritone) — A* 寻路引擎
- [深度求索](https://chat.deepseek.com/) — Deepseek
- [MaLiLib](https://github.com/maruohon/malilib) — 配置 GUI 框架
- [Fabric](https://fabricmc.net/) — 模组加载器
- [LiquidBounce](https://github.com/CCBlueX/LiquidBounce) — 参考代码

---

## 许可证

MIT License
