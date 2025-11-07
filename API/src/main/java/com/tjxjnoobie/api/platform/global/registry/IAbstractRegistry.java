package com.tjxjnoobie.api.platform.global.registry;

import com.tjxjnoobie.api.platform.global.registry.enums.RegistryType;
import com.tjxjnoobie.api.platform.global.registry.metadata.interfaces.IRegistryData;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Base interface for registry containers that map RegistryType to instances/settings.
 * @param <REG_TYPE> the element type held by the registry instances
 */
public interface IAbstractRegistry<REG_TYPE extends RegistryType<REG_TYPE,REG_DATA>, REG_DATA extends IRegistryData> {





    default RegistryType<REG_TYPE,REG_DATA> getRegistryByType(REG_TYPE registryType){
        return null;
    }



    default IAbstractRegistry<REG_TYPE, REG_DATA> createRegistry(REG_TYPE registryType, REG_DATA registryData){
        return this;
    }


    default REG_TYPE getRegistryType(){
        return null;
    }

    default RegistryType<REG_TYPE, REG_DATA> getRegistryTypeByData(REG_DATA registryData){
        return null;
    }

    default REG_DATA getRegistryData(REG_TYPE registryType){
        return null;
    }

    default IAbstractRegistry<REG_TYPE, REG_DATA> getAbstractRegistry(){
        return null;
    }

    default IRegistryData<REG_TYPE, REG_DATA> getRegistryData(REG_DATA registryData){
        return null;
    }


    default Set<REG_TYPE> getRegistryTypesAsSet(){
        return  null;
    }

   default List<REG_TYPE> getRegistryTypesAsList(){
        return null;
   }

    default Collection<REG_TYPE> getRegistryTypesAsCollection(){
         return null;
    }

    @SuppressWarnings("unchecked")
   default REG_TYPE[] getRegistryTypesAsArray(){
        return null;
    }

    default Set<REG_DATA> getRegistryDataBySet(){
        return null;
    }

    default List<REG_DATA> getRegistryDatasByList(){
        return null;
    }

    default Collection<REG_DATA> getRegistryDataByCollection(){
        return null;
    }

    @SuppressWarnings("unchecked")
    default REG_DATA[] getRegistryDataByArray(){
        return null;
    }

    default boolean hasRegistryType(REG_TYPE registryType){
        return false;
    }

    default boolean hasRegistryData(REG_DATA registryData){
        return false;
    }
}