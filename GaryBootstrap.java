package me.piggypiglet.gary;

import com.google.inject.Injector;
import me.piggypiglet.gary.guice.ProviderSetterModule;
import me.piggypiglet.gary.guice.InitializationModule;
import me.piggypiglet.gary.guice.Providers;
import me.piggypiglet.gary.registerables.Registerable;
import me.piggypiglet.gary.registerables.implementations.JDARegisterable;
import me.piggypiglet.gary.registerables.implementations.SecondRegisterable;
import net.dv8tion.jda.api.JDA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

// ------------------------------
// Copyright (c) PiggyPiglet 2019
// https://www.piggypiglet.me
// ------------------------------
public final class GaryBootstrap {
    private GaryBootstrap() {
        InitializationModule module = new InitializationModule(getClass());
        AtomicReference<Injector> injector = new AtomicReference<>(module.createInjector());

        Stream.of(JDARegisterable.class, SecondRegisterable.class).forEach(r -> {
            Registerable registerable = injector.get().getInstance(r);
            registerable.run();

            if (registerable.getProviders().size() > 0) {
                injector.set(injector.get().createChildInjector(new ProviderSetterModule(handleProviders(registerable.getProviders()))));
            }
        });

        injector.get().getInstance(Test.class).test();
    }

    private Map<Class, Object> handleProviders(List<Object> providers) {
        Map<Class, Object> map = new HashMap<>();

        providers.forEach(o -> {
            switch (Providers.fromClass(o.getClass())) {
                case JDA:
                    map.put(JDA.class, o);
                    break;
            }
        });

        return map;
    }

    public static void main(String[] args) {
        new GaryBootstrap();
    }
}
