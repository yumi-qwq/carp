package baritone;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.Settings;
import baritone.api.behavior.IBehavior;
import baritone.api.event.listener.IEventBus;
import baritone.api.process.IBaritoneProcess;
import baritone.api.process.IElytraProcess;
import baritone.api.utils.IPlayerContext;
import baritone.behavior.*;
import baritone.cache.WorldProvider;
import baritone.event.GameEventHandler;
import baritone.process.*;
import baritone.process.elytra.NullElytraProcess;
import baritone.selection.SelectionManager;
import baritone.utils.BlockStateInterface;
import baritone.utils.InputOverrideHandler;
import baritone.utils.player.BaritonePlayerContext;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Baritone implements IBaritone {

    private static final ThreadPoolExecutor threadPool;

    static {
        threadPool = new ThreadPoolExecutor(4, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
    }

    private final Minecraft mc;
    private final Path directory;
    private final GameEventHandler gameEventHandler;

    private final PathingBehavior pathingBehavior;
    private final LookBehavior lookBehavior;
    private final InventoryBehavior inventoryBehavior;
    private final InputOverrideHandler inputOverrideHandler;

    private final FollowProcess followProcess;
    private final CustomGoalProcess customGoalProcess;
    private final ExploreProcess exploreProcess;
    private final IElytraProcess elytraProcess;
    private final SelectionManager selectionManager;

    private final IPlayerContext playerContext;
    private final WorldProvider worldProvider;

    public BlockStateInterface bsi;

    Baritone(Minecraft mc) {
        this.mc = mc;
        this.gameEventHandler = new GameEventHandler(this);

        this.directory = mc.gameDirectory.toPath().resolve("baritone");
        if (!Files.exists(this.directory)) {
            try {
                Files.createDirectories(this.directory);
            } catch (IOException ignored) {}
        }

        this.playerContext = new BaritonePlayerContext(this, mc);

        {
            this.lookBehavior         = this.registerBehavior(LookBehavior::new);
            this.pathingBehavior      = this.registerBehavior(PathingBehavior::new);
            this.inventoryBehavior    = this.registerBehavior(InventoryBehavior::new);
            this.inputOverrideHandler = this.registerBehavior(InputOverrideHandler::new);
            this.registerBehavior(WaypointBehavior::new);
        }

        {
            this.followProcess           = this.registerProcess(FollowProcess::new);
            this.customGoalProcess       = this.registerProcess(CustomGoalProcess::new);
            this.exploreProcess          = this.registerProcess(ExploreProcess::new);
            this.elytraProcess           = this.registerProcess(NullElytraProcess::new);
        }

        this.worldProvider = new WorldProvider(this);
        this.selectionManager = new SelectionManager(this);
    }

    public void registerBehavior(IBehavior behavior) {
        this.gameEventHandler.registerEventListener(behavior);
    }

    public <T extends IBehavior> T registerBehavior(Function<Baritone, T> constructor) {
        final T behavior = constructor.apply(this);
        this.registerBehavior(behavior);
        return behavior;
    }

    public <T extends IBaritoneProcess> T registerProcess(Function<Baritone, T> constructor) {
        final T behavior = constructor.apply(this);
        // PathingControlManager removed in MC 26.1 port
        return behavior;
    }

    @Override public IPlayerContext getPlayerContext() { return this.playerContext; }
    @Override public FollowProcess getFollowProcess() { return this.followProcess; }
    @Override public CustomGoalProcess getCustomGoalProcess() { return this.customGoalProcess; }
    @Override public ExploreProcess getExploreProcess() { return this.exploreProcess; }
    @Override public PathingBehavior getPathingBehavior() { return this.pathingBehavior; }
    @Override public LookBehavior getLookBehavior() { return this.lookBehavior; }
    @Override public SelectionManager getSelectionManager() { return selectionManager; }
    @Override public WorldProvider getWorldProvider() { return this.worldProvider; }
    @Override public IEventBus getGameEventHandler() { return this.gameEventHandler; }
    @Override public InputOverrideHandler getInputOverrideHandler() { return this.inputOverrideHandler; }

    public InventoryBehavior getInventoryBehavior() { return this.inventoryBehavior; }
    public IElytraProcess getElytraProcess() { return this.elytraProcess; }
    public Path getDirectory() { return this.directory; }

    public static Settings settings() { return BaritoneAPI.getSettings(); }
    public static Executor getExecutor() { return threadPool; }
}
