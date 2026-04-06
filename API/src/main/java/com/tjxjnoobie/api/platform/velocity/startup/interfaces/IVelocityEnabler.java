/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.velocity.startup.interfaces;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;

public interface IVelocityEnabler extends IVelocityMain, com.tjxjnoobie.api.dependency.IDependencyInjectableInterface {

    @Subscribe
    void onVelocityEnable(ProxyInitializeEvent e) throws Throwable;


}
