package com.chyzman.electromechanics.logic.api;

import com.chyzman.electromechanics.Electromechanics;
import com.chyzman.electromechanics.logic.IOConfigurations;
import com.chyzman.electromechanics.logic.api.configuration.Side;
import com.chyzman.electromechanics.logic.api.configuration.SignalConfiguration;
import com.chyzman.electromechanics.logic.api.configuration.SignalType;
import com.chyzman.electromechanics.logic.api.state.GateStateStorage;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.KeyedEndec;
import net.minecraft.util.ActionResult;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Map;

public class TimerGateHandlers {

    public static final KeyedEndec<Integer> TIMER_INTERVAL = Endec.INT.keyed("TimerInterval", 0);
    public static final KeyedEndec<Integer> PAST_TICK = Endec.INT.keyed("PastTick", 0);

    public static final KeyedEndec<Boolean> IS_LOCKED = Endec.BOOLEAN.keyed("IsLocked", false);
    public static final KeyedEndec<Boolean> IS_OUTPUTTING = Endec.BOOLEAN.keyed("IsOutputting", false);

    public static final GateHandler TIMER = GateHandler.of(
            Electromechanics.id("timer"),
            "⏲",
            IOConfigurations.MONO_TO_MONO,
            GateOutputFunction.singleExpression((context, integers) -> {
                var map = context.storage().dynamicStorage();

                var isPowered = integers[0] > 0;
                var isLocked = map.get(IS_LOCKED);

                if(isPowered && !isLocked){
                    map.put(IS_LOCKED, true);
                    isLocked = true;
                } else if(!isPowered && isLocked){
                    map.put(IS_LOCKED, false);
                    isLocked = false;
                }

                if(isLocked) return 0;

                return BooleanUtils.toInteger(context.storage().dynamicStorage().get(IS_OUTPUTTING));
            }),
            storage -> {
                var map = storage.dynamicStorage();

                if(!map.has(TIMER_INTERVAL)) map.put(TIMER_INTERVAL, 1);
                //map.put(GateStateStorage.UPDATE_DELAY, 0);
            },
            context -> {
                var nextMode = context.getMode() + 1;

                if(nextMode > 41) nextMode = 0;

                context.setMode(nextMode);

                return ActionResult.SUCCESS;
            },
            (handler, context) -> {
                var storage = context.storage();
                var map = storage.dynamicStorage();

                if(map.get(IS_LOCKED)) {
                    if(map.get(PAST_TICK) != 0) map.put(PAST_TICK, 0);

                    return ActionResult.FAIL;
                }

                var tick = map.get(PAST_TICK) + 1;

                var interval = storage.dynamicStorage().get(TIMER_INTERVAL);

                if(interval < 1) interval = 1;

                var betweenPulseDelay = (storage.getMode() + 1) * (long) interval;

                var result = ActionResult.PASS;

                var resetTick = tick > betweenPulseDelay + handler.getUpdateDelay(context);

                if((betweenPulseDelay == 1) ? tick == 1 : tick % betweenPulseDelay == 0){
                    map.put(IS_OUTPUTTING, true);

                    result = ActionResult.SUCCESS;
                } else if(map.get(IS_OUTPUTTING) && resetTick) {
                    map.put(IS_OUTPUTTING, false);
                    result = ActionResult.SUCCESS;
                }

                if(resetTick) tick = 0;

                map.put(PAST_TICK, tick);

                return result;
            }
    );

    private static final SignalConfiguration CONFIGURATION = new SignalConfiguration(
            Map.of(
                Side.LEFT, SignalType.ANALOG,
                Side.BACK, SignalType.DIGITAL,
                Side.RIGHT, SignalType.ANALOG,
                Side.FRONT, SignalType.DIGITAL
            )
    );

    public static final GateHandler ADVANCED_TIMER = GateHandler.of(
            Electromechanics.id("advanced_timer"),
            "⏲",
            IOConfigurations.TRI_TO_MONO,
            GateOutputFunction.singleExpression((context, integers) -> {
                var map = context.storage().dynamicStorage();

                var isPowered = integers[1] > 0;
                var isLocked = map.get(IS_LOCKED);

                if(isPowered && !isLocked){
                    map.put(IS_LOCKED, true);
                    isLocked = true;
                } else if(!isPowered && isLocked){
                    map.put(IS_LOCKED, false);
                    isLocked = false;
                }

                if(isLocked) return 0;

                return BooleanUtils.toInteger(context.storage().dynamicStorage().get(IS_OUTPUTTING));
            }),
            storage -> {
                var map = storage.dynamicStorage();

                map.put(GateStateStorage.SIGNAL_CONFIGURATION, CONFIGURATION);
                //map.put(GateStateStorage.UPDATE_DELAY, 0);
            },
            context -> {
                var nextMode = context.getMode() + 1;

                if(nextMode > 21) nextMode = 0;

                context.setMode(nextMode);

                return ActionResult.SUCCESS;
            },
            (handler, context) -> {
                var storage = context.storage();
                var map = storage.dynamicStorage();

                if(map.get(IS_LOCKED)) {
                    if(map.get(PAST_TICK) != 0) map.put(PAST_TICK, 0);

                    return ActionResult.FAIL;
                }

                var leftInput = storage.getInputPower(Side.LEFT);
                var rightInput = storage.getInputPower(Side.RIGHT);

                var tick = map.get(PAST_TICK) + 1;

                var betweenPulseDelay = (storage.getMode() + 1) * (leftInput + 1) * (rightInput + 1);

                var result = ActionResult.PASS;

                var resetTick = tick > betweenPulseDelay + handler.getUpdateDelay(context);

                if((betweenPulseDelay == 1) ? tick == 1 : tick % betweenPulseDelay == 0){
                    map.put(IS_OUTPUTTING, true);

                    result = ActionResult.SUCCESS;
                } else if(map.get(IS_OUTPUTTING) && resetTick) {
                    map.put(IS_OUTPUTTING, false);
                    result = ActionResult.SUCCESS;
                }

                if(resetTick) tick = 0;

                map.put(PAST_TICK, tick);

                return result;
            }
    );
}
