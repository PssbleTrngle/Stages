package com.possible_triangle.stages;

import com.google.common.base.Stopwatch;
import net.minecraft.Util;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ActiveProfiler;
import net.minecraft.util.profiling.ProfileResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class DebugReloadInstance extends SimpleReloadInstance<DebugReloadInstance.State>  {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Stopwatch total = Stopwatch.createUnstarted();

    public DebugReloadInstance(ResourceManager $$0, List<PreparableReloadListener> $$1, Executor $$2, Executor $$3, CompletableFuture<Unit> $$4) {
        super($$2, $$3, $$0, $$1, ($$1x, $$2x, $$3x, $$4x, $$5) -> {
            AtomicLong $$6 = new AtomicLong();
            AtomicLong $$7 = new AtomicLong();
            ActiveProfiler $$8 = new ActiveProfiler(Util.timeSource, () -> {
                return 0;
            }, false);
            ActiveProfiler $$9 = new ActiveProfiler(Util.timeSource, () -> {
                return 0;
            }, false);
            LOGGER.info("Started reloading " + $$3x.getName());
            CompletableFuture<Void> $$10 = $$3x.reload($$1x, $$2x, $$8, $$9, ($$2xx) -> {
                $$4x.execute(() -> {
                    long n = Util.getNanos();
                    $$2xx.run();
                    LOGGER.info("Finished preparing " + $$3x.getName());
                    $$6.addAndGet(Util.getNanos() - n);
                });
            }, ($$2xx) -> {
                $$5.execute(() -> {
                    long n = Util.getNanos();
                    $$2xx.run();
                    $$7.addAndGet(Util.getNanos() - n);
                });
            });
            return $$10.thenApplyAsync(($$5x) -> {
                LOGGER.info("Finished reloading " + $$3x.getName());
                return new State($$3x.getName(), $$8.getResults(), $$9.getResults(), $$6, $$7);
            }, $$3);
        }, $$4);
        this.total.start();
        this.allDone = this.allDone.thenApplyAsync(this::finish, $$3);
    }

    private List<State> finish(List<State> $$0x) {
        this.total.stop();
        int $$1x = 0;
        LOGGER.info("Resource reload finished after {} ms", this.total.elapsed(TimeUnit.MILLISECONDS));

        int $$6;
        for(Iterator var3 = $$0x.iterator(); var3.hasNext(); $$1x += $$6) {
            State $$2x = (State)var3.next();
            ProfileResults $$3x = $$2x.preparationResult;
            ProfileResults $$4x = $$2x.reloadResult;
            int $$5 = (int)((double)$$2x.preparationNanos.get() / 1000000.0);
            $$6 = (int)((double)$$2x.reloadNanos.get() / 1000000.0);
            int $$7 = $$5 + $$6;
            String $$8 = $$2x.name;
            LOGGER.info("{} took approximately {} ms ({} ms preparing, {} ms applying)", new Object[]{$$8, $$7, $$5, $$6});
        }

        LOGGER.info("Total blocking time: {} ms", $$1x);
        return $$0x;
    }

    public static class State {
        final String name;
        final ProfileResults preparationResult;
        final ProfileResults reloadResult;
        final AtomicLong preparationNanos;
        final AtomicLong reloadNanos;

        State(String $$0, ProfileResults $$1, ProfileResults $$2, AtomicLong $$3, AtomicLong $$4) {
            this.name = $$0;
            this.preparationResult = $$1;
            this.reloadResult = $$2;
            this.preparationNanos = $$3;
            this.reloadNanos = $$4;
        }
    }

}
