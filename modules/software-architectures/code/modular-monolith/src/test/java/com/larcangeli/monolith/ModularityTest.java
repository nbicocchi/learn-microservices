package com.larcangeli.monolith;

import org.junit.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ModularityTest {
    static ApplicationModules modules = ApplicationModules.of(ModularMonolithApp.class);

    @Test
    public void verifyModularStructure(){
        modules.forEach(System.out::println);
        modules.verify();
    }

    @Test
    public void createModuleDocumentation(){
        new Documenter(modules).writeDocumentation().writeIndividualModulesAsPlantUml();
    }
}
