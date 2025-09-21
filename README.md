**View technical assesment of this project here[https://github.com/tjXJNOOBIE/PROJECT-NOVUS/blob/master/COMPREHENSIVE_TECH_ASSESSMENT.md]**

In development **solo** project with ambitious goals.

Current buiit/in-progress systems: 
- **_GlickoV2 Chess ELO_** – Unlimited **_match_** and **_player simulations_** for skill-based rating calculations.
  <details>
    <summary>Click to Show Rating Simulation GIF & Log</summary>
    <text>Fresh Player starts with default rating (1500) deviation (350) and volatility (0.06)</text>
    <text>Player 1-3 has starts with random rating deviation and volatility </text>
    
    ![Ratings GIF](https://github.com/tjXJNOOBIE/PROJECT-NOVUS/blob/master/media/ratings.gif)
    ![Simulation Log](https://github.com/tjXJNOOBIE/PROJECT-NOVUS/blob/master/media/rating_consol.png)

  </details>
- **_Custom Retention Calculator_** – Generates **_Retention Ratings_** per player, including **_creator code  & purchase tracking_**.  
  <details>
    <summary>Click to Show Rentention Rating Tabke & Equation</summary>
    <text>Mock data from simulation</text>
    
    ![MySQL retention data](https://github.com/tjXJNOOBIE/PROJECT-NOVUS/blob/master/media/retention.png)
    <text> Retention Equation:
           p = purchaseAmount (dollars in last 14 days)
           t = timePlayed (hours in last 30 days)
           r = returnJoins (joins in last 30 days)
           n = numberOfPurchases (count in last 14 days)</text>
    
    ![Retention Calculator](https://github.com/tjXJNOOBIE/PROJECT-NOVUS/blob/master/media/retention_calc.png)

  </details>
  
- **_MySQL-Based World Manager_** – Centralized storage and retrieval of world data with transactional safety.
  <details>
    <summary>Click to Show Rentention Rating Tabke & Equation</summary>
    <text>World data from MySQL. XYZ+P+Y = Spawn Location</text>
    
    ![MySQL World Manager](https://github.com/tjXJNOOBIE/PROJECT-NOVUS/blob/master/media/world_mysql.png)
    
  </details>
  
- **_External Library Loader_** – Load libraries from a custom /libs folder in any environment.  
  <details>
    <summary>Click to Show Libraries Loading</summary>
    <text>Future: Auto Library Updates and version verification to match project libraries</text>
    
    ![Libraries Loading](https://github.com/tjXJNOOBIE/PROJECT-NOVUS/blob/master/media/library_loading.png)
    
  </details>
- **_Custom Ranks_** – String based permission with power-level rankings with **_MySQL support_** for persistent player data.  
  <details>
    <summary>Click to Show Libraries Loading</summary>
    <text>In-Game commands with "Rank Not Found" edge case </text>
    
    ![In-Game Commands](https://github.com/tjXJNOOBIE/PROJECT-NOVUS/blob/master/media/rankcmd_usage.png)

    <text>MySQL Storage /w String based permissions & power-level</text>
    ![MySQL Ranks Table](https://github.com/tjXJNOOBIE/PROJECT-NOVUS/blob/master/media/ranks_mysql.png)

  </details>
- **_Strict Method Contracting_** - All relevant methods are contracted in interfaces, interfaces register themselves to themselves.
  <details>
    <summary>Click to Show Interface Registration</summary>
    <text>Interface Registration</text>
    
    ![Interface Registration](https://github.com/tjXJNOOBIE/PROJECT-NOVUS/blob/master/media/class_register_from_context.png)
    <text>Register Method Logic, uses Maps for Dependency Management</text>
    ![Register Method Logic](https://github.com/tjXJNOOBIE/PROJECT-NOVUS/blob/master/media/registerMethod.png)
  </details>
- **_Runtime Flexibility__** - Implementations can be loaded, replaced, or removed without touching consumers
  <details>
    <summary>Click to Show DI Reload Method</summary>
    <text>Interface Registration</text>
    
    ![DI Reload](https://github.com/tjXJNOOBIE/PROJECT-NOVUS/blob/master/media/depend_reload.png)
  </details>
  
- **_Fully Decoupled_** - Interface/DI management allow for gurnarateed behavior, consumer interacts via interfaced method contracts, no concrete calls.
- **_Default Method Usage_** - Usage of default methods to allow concrete classes to implement multiple interfaces
- **_Type Safe & Abstract Flexibility_** - Type-Safe abstractions, reusable components and strict contracts with flexibility
- **_Live Multiplayer Speedrun_** – **_Game loop_**, **_per-player same-seed worlds_**, and **_event tracking_** for synchronized speedrun sessions.  
- **_Multi-Module Project_** – **_Paper_** and **_Velocity runtimes_** handled in a **_single JAR_** for unified deployment.  
- **_Custom Dependency Injection_** – Uses custom **_`@Inject`_** and **_`@AutoInjectAll`_** annotations for modular wiring.  
- **_Method Tagging via `@ModuleScope`_** – Tracks the **_domain_** where a method executes for improved debugging and analytics.  
- **_Robust Exception Handling_** – Custom **_logging_** for critical errors with detailed context output.  
- **_Voting System with GUI_** – Interactive **_vote handling_** with an in-game graphical interface.  
- **_Rainbow Boss Bar_** – Dynamic **_color-shifting boss bar_** for enhanced visual feedback.  
- **_Debugging with Network/Local Server Toggles_** – Seamless switching between **_development_** and **_production environments_**.  
- **_PaperAPI Event Handling_** – Integrated **_event processing_** using Paper’s high-performance API.  
- **_Fully Decoupled Classes_** – **_Interface contracts_** are the primary method handling mechanism for maintainability.  
- **_Abstraction Layers_** – Abstracted **_Cache_**, **_Events_**, **_Context_**, **_Commands_**, and other systems for reusability.  
- **_Context-Based Dependency Injection_** – Domain-specific **_contexts_** for clean separation of concerns.  
- **_Custom Logging of All Events_** – Comprehensive tracking for debugging and auditing.  
- **_Robust Event Tagging_** – Tracks **_source_**, **_status_**, **_domain_**, **_priority_**, and **_capability_** for every event.  
- **_Custom Event System_** – Includes **_EventBus_** and **_event tags_** for modular communication.  
- **_Internal and External Cache Pipelines_** – **_Internal (RAM) → Redis → MySQL_** for multi-layered caching.  
  
- **_Internal API_** – Unified **_API layer_** for inter-module communication and external integrations.  
- **_Punishment System_** – Integrated with **_Redis_** and **_MySQL_** for real-time moderation.  
- **_Robust InventoryManager_** – Built using **_Builder patterns_** for flexible and safe inventory handling.  


Planned systems (At a glance): _Subject to change_

## Gameplay Systems
- **_Castle Placement & Validation_** – Complete placement visuals, region previews, and server-side checks across **_versions 1.8–1.21_**.  
- **_Troop Logic Framework_** – Tiered units with **_stats_**, **_behaviors_**, **_movement_**, and **_raid mechanics_** tied into resource and economy flows.  
- **_Companion System_** – **_Levelable allies_** with type-specific **_skills_** and **_stat progression_**, designed for persistent player growth.  
- **_Building Upgrade Paths_** – Modular **_structures_** with **_stat unlocks_** and **_purpose-driven progression_** to drive mid/late game depth.  
- **_Kingdom State Machine_** – **_PROTECTED_**, **_OPEN_**, **_CLOSED_**, and **_KINGDOM_VS_KINGDOM_** states with graceful failover handling.  
- **_Command Mode UI & Inventory Manager_** – Custom **_interfaces_** for **_troop commands_** and advanced **_inventory interactions_**.  

## Combat & Economy
- **_PvP/PvE Combat Engine_** – **_Siege mechanics_**, **_pillaging_**, **_raid rewards_**, and synchronized **_troop behaviors_**.  
- **_Multi-Currency Economy_** – **_Main currency_**, **_troop currency_**, **_shards_**, and **_dust_** with **_item conversion logic_** and **_vault rewards_**.  

## Guild & Social Features
- **_Guild Hierarchy_** – **_Ranks_**, **_permissions_**, **_logos_**, **_invites_**, and **_messaging_**.  

## Events & Concurrency
- **_Abstract Event Bus_** – Custom **_event system_** with **_annotations_** (`@KingdomFactions`, `@Core`) and **_setter-style configuration_**.  
- **_Global & Game Events_** – From **_MySQLDisconnectEvent_** to **_TroopMarchEvent_**, including built-in **_.wait()_** / **_.notify()_** helpers.  

## Config & Context Handling
- **_Type-Safe Config Keys_** – Enum-based **_IConfigKey_** with **_BukkitConfigBridge_** adapter and custom **_exceptions_** for bad paths.  
- **_Dependency Injection Framework_** – **_AbstractContext_** and **_IGlobalContext_** for runtime **_class-to-interface binding_**, no external instantiation required.  

## Distributed Architecture for Mass Scaling
- **_Command Center Nodes (C1–C5)_** – Full **_failover design_**: **_C1 fallback node_** through **_C5 multi-cloud orchestrator_**.  
- **_C6 Vision_** – **_AI-assisted_**, **_multi-region_** **_cluster-of-clusters decisioning_** to future-proof large-scale deployments.  
- **_Distributed Server Option_** – **_Any logic_** can offload to a separate server for scalability and performance.  
- **_Self-Hosted Control Panels_** – Clients can deploy all **_dashboards locally_** for full autonomy.  

## Data & Caching
- **_Cache Pipeline_** – **_RAM → Redis (TTL/dirty tags) → MySQL → Flatfile or SQLITE → S3_** with **_race condition handling_**.  

## Version Support
- **_Version Abstraction Layer_** – Unified **_particle/sound handling_** for **_1.8–1.21+_** plus **_backported and/or custom mobs and AI_**.  
