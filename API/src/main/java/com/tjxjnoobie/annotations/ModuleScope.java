package com.tjxjnoobie.annotations;

import com.tjxjnoobie.enums.EventDomain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ModuleScope {


    EventDomain value(); // e.g., "Core", "KingdomFactions"
}