package baritone;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.IBaritoneProvider;
import baritone.api.cache.IWorldProvider;
import net.minecraft.client.Minecraft;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Minimal BaritoneProvider for MC 26.1 port.
 * Only supports the primary (local player) baritone instance.
 */
public class BaritoneProvider implements IBaritoneProvider {

    private Baritone primary;

    @Override
    public IBaritone getPrimaryBaritone() {
        if (primary == null) {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null) {
                primary = (Baritone) createBaritone(mc);
            }
        }
        return primary;
    }

    @Override
    public List<IBaritone> getAllBaritones() {
        if (primary == null) getPrimaryBaritone();
        return primary != null ? Collections.singletonList(primary) : Collections.emptyList();
    }

    @Override
    public IBaritone createBaritone(Minecraft mc) {
        if (primary != null && Objects.equals(mc, primary.getPlayerContext().minecraft())) {
            return primary;
        }
        primary = new Baritone(mc);
        return primary;
    }

    @Override
    public boolean destroyBaritone(IBaritone baritone) {
        if (baritone == primary) {
            primary = null;
            return true;
        }
        return false;
    }

    @Override
    public baritone.api.command.ICommandSystem getCommandSystem() {
        return null; // CommandSystem removed in MC 26.1 port
    }
}
