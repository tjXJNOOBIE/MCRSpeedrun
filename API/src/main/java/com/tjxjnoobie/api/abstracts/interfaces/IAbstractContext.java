package com.tjxjnoobie.api.abstracts.interfaces;

public interface IAbstractContext {
    <U> void register(Class<U> clazz, U instance);


}
