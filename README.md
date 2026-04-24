<!-- tavall:badges:start -->
[![Org](https://img.shields.io/badge/org-TavallStudios-181717?logo=github)](https://github.com/TavallStudios) [![Stack](https://img.shields.io/badge/stack-Maven-0A7BBB)](https://api.github.com/repos/TavallStudios/MCRSpeedrun) ![History](https://img.shields.io/badge/history-preserved-6f42c1)
<!-- tavall:badges:end -->

﻿**View technical assessment of this project here: [/docs/COMPREHENSIVE_TECH_ASSESSMENT.md](/docs/COMPREHENSIVE_TECH_ASSESSMENT.md)**

In development **solo** project with ambitious goals.

Current buiit/in-progress systems: 
- **_GlickoV2 Chess ELO_** â€“ Unlimited **_match_** and **_player simulations_** for skill-based rating calculations.
  <details>
    <summary>Click to Show Rating Simulation GIF & Log</summary>
    <text>Fresh Player starts with default rating (1500) deviation (350) and volatility (0.06)</text>
    <text>Player 1-3 has starts with random rating deviation and volatility </text>
    
    ![Ratings GIF](/docs/media/ratings.gif)
    ![Simulation Log](/docs/media/rating_consol.png)

  </details>
- **_Custom Retention Calculator_** â€“ Generates **_Retention Ratings_** per player, including **_creator code  & purchase tracking_**.  
  <details>
    <summary>Click to Show Rentention Rating Tabke & Equation</summary>
    <text>Mock data from simulation</text>
    
    ![MySQL retention data](/docs/media/retention.png)
    <text> Retention Equation:
           p = purchaseAmount (dollars in last 14 days)
           t = timePlayed (hours in last 30 days)
           r = returnJoins (joins in last 30 days)
           n = numberOfPurchases (count in last 14 days)</text>
    
    ![Retention Calculator](/docs/media/retention_calc.png)

  </details>
  
- **_MySQL-Based World Manager_** â€“ Centralized storage and retrieval of world data with transactional safety.
  <details>
    <summary>Click to Show Rentention Rating Tabke & Equation</summary>
    <text>World data from MySQL. XYZ+P+Y = Spawn Location</text>
    
    ![MySQL World Manager](/docs/media/world_mysql.png)
    
  </details>
  
- **_External Library Loader_** â€“ Load libraries from a custom /libs folder in any environment.  
  <details>
    <summary>Click to Show Libraries Loading</summary>
    <text>Future: Auto Library Updates and version verification to match project libraries</text>
    
    ![Libraries Loading](/docs/media/library_loading.png)
    
  </details>
- **_Custom Ranks_** â€“ String based permission with power-level rankings with **_MySQL support_** for persistent player data.  
  <details>
    <summary>Click to Show Libraries Loading</summary>
    <text>In-Game commands with "Rank Not Found" edge case </text>
    
    ![In-Game Commands](/docs/media/rankcmd_usage.png)

    <text>MySQL Storage /w String based permissions & power-level</text>
    ![MySQL Ranks Table](/docs/media/ranks_mysql.png)

  </details>
- **_Strict Method Contracting_** - All relevant methods are contracted in interfaces, interfaces register themselves to themselves.
  <details>
    <summary>Click to Show Interface Registration</summary>
    <text>Interface Registration</text>
    
    ![Interface Registration](/docs/media/class_register_from_context.png)
    <text>Register Method Logic, uses Maps for Dependency Management</text>
    ![Register Method Logic](/docs/media/registerMethod.png)
  </details>
- **_Runtime Flexibility__** - Implementations can be loaded, replaced, or removed without touching consumers
  <details>
    <summary>Click to Show DI Reload Method</summary>
    <text>Interface Registration</text>
    
    ![DI Reload](/docs/media/depend_reload.png)
  </details>
  
- **_Fully Decoupled_** - Interface/DI management allow for gurnarateed behavior, consumer interacts via interfaced method contracts, no concrete calls.
- **_Default Method Usage_** - Usage of default methods to allow concrete classes to implement multiple interfaces
- **_Type Safe & Abstract Flexibility_** - Type-Safe abstractions, reusable components and strict contracts with flexibility
- **_Live Multiplayer Speedrun_** â€“ **_Game loop_**, **_per-player same-seed worlds_**, and **_event tracking_** for synchronized speedrun sessions.  
- **_Multi-Module Project_** â€“ **_Paper_** and **_Velocity runtimes_** handled in a **_single JAR_** for unified deployment.  
- **_Custom Dependency Injection_** â€“ Uses custom **_`@Inject`_** and **_`@AutoInjectAll`_** annotations for modular wiring.  
- **_Method Tagging via `@ModuleScope`_** â€“ Tracks the **_domain_** where a method executes for improved debugging and analytics.  
- **_Robust Exception Handling_** â€“ Custom **_logging_** for critical errors with detailed context output.  
- **_Voting System with GUI_** â€“ Interactive **_vote handling_** with an in-game graphical interface.  
- **_Rainbow Boss Bar_** â€“ Dynamic **_color-shifting boss bar_** for enhanced visual feedback.  
- **_Debugging with Network/Local Server Toggles_** â€“ Seamless switching between **_development_** and **_production environments_**.  
- **_PaperAPI Event Handling_** â€“ Integrated **_event processing_** using Paperâ€™s high-performance API.  
- **_Fully Decoupled Classes_** â€“ **_Interface contracts_** are the primary method handling mechanism for maintainability.  
- **_Abstraction Layers_** â€“ Abstracted **_Cache_**, **_Events_**, **_Context_**, **_Commands_**, and other systems for reusability.  
- **_Context-Based Dependency Injection_** â€“ Domain-specific **_contexts_** for clean separation of concerns.  
- **_Custom Logging of All Events_** â€“ Comprehensive tracking for debugging and auditing.  
- **_Robust Event Tagging_** â€“ Tracks **_source_**, **_status_**, **_domain_**, **_priority_**, and **_capability_** for every event.  
- **_Custom Event System_** â€“ Includes **_EventBus_** and **_event tags_** for modular communication.  
- **_Internal and External Cache Pipelines_** â€“ **_Internal (RAM) â†’ Redis â†’ MySQL_** for multi-layered caching.  
  
- **_Internal API_** â€“ Unified **_API layer_** for inter-module communication and external integrations.  
- **_Punishment System_** â€“ Integrated with **_Redis_** and **_MySQL_** for real-time moderation.  
- **_Robust InventoryManager_** â€“ Built using **_Builder patterns_** for flexible and safe inventory handling.  


Planned systems (At a glance): _Subject to change_

## Gameplay Systems
- **_Castle Placement & Validation_** â€“ Complete placement visuals, region previews, and server-side checks across **_versions 1.8â€“1.21_**.  
- **_Troop Logic Framework_** â€“ Tiered units with **_stats_**, **_behaviors_**, **_movement_**, and **_raid mechanics_** tied into resource and economy flows.  
- **_Companion System_** â€“ **_Levelable allies_** with type-specific **_skills_** and **_stat progression_**, designed for persistent player growth.  
- **_Building Upgrade Paths_** â€“ Modular **_structures_** with **_stat unlocks_** and **_purpose-driven progression_** to drive mid/late game depth.  
- **_Kingdom State Machine_** â€“ **_PROTECTED_**, **_OPEN_**, **_CLOSED_**, and **_KINGDOM_VS_KINGDOM_** states with graceful failover handling.  
- **_Command Mode UI & Inventory Manager_** â€“ Custom **_interfaces_** for **_troop commands_** and advanced **_inventory interactions_**.  

## Combat & Economy
- **_PvP/PvE Combat Engine_** â€“ **_Siege mechanics_**, **_pillaging_**, **_raid rewards_**, and synchronized **_troop behaviors_**.  
- **_Multi-Currency Economy_** â€“ **_Main currency_**, **_troop currency_**, **_shards_**, and **_dust_** with **_item conversion logic_** and **_vault rewards_**.  

## Guild & Social Features
- **_Guild Hierarchy_** â€“ **_Ranks_**, **_permissions_**, **_logos_**, **_invites_**, and **_messaging_**.  

## Events & Concurrency
- **_Abstract Event Bus_** â€“ Custom **_event system_** with **_annotations_** (`@KingdomFactions`, `@Core`) and **_setter-style configuration_**.  
- **_Global & Game Events_** â€“ From **_MySQLDisconnectEvent_** to **_TroopMarchEvent_**, including built-in **_.wait()_** / **_.notify()_** helpers.  

## Config & Context Handling
- **_Type-Safe Config Keys_** â€“ Enum-based **_IConfigKey_** with **_BukkitConfigBridge_** adapter and custom **_exceptions_** for bad paths.  
- **_Dependency Injection Framework_** â€“ **_AbstractContext_** and **_IGlobalContext_** for runtime **_class-to-interface binding_**, no external instantiation required.  

## Distributed Architecture for Mass Scaling
- **_Command Center Nodes (C1â€“C5)_** â€“ Full **_failover design_**: **_C1 fallback node_** through **_C5 multi-cloud orchestrator_**.  
- **_C6 Vision_** â€“ **_AI-assisted_**, **_multi-region_** **_cluster-of-clusters decisioning_** to future-proof large-scale deployments.  
- **_Distributed Server Option_** â€“ **_Any logic_** can offload to a separate server for scalability and performance.  
- **_Self-Hosted Control Panels_** â€“ Clients can deploy all **_dashboards locally_** for full autonomy.  

## Data & Caching
- **_Cache Pipeline_** â€“ **_RAM â†’ Redis (TTL/dirty tags) â†’ MySQL â†’ Flatfile or SQLITE â†’ S3_** with **_race condition handling_**.  

## Version Support
- **_Version Abstraction Layer_** â€“ Unified **_particle/sound handling_** for **_1.8â€“1.21+_** plus **_backported and/or custom mobs and AI_**.  

