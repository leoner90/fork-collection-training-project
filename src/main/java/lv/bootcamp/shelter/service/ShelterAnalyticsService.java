package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;
import lv.bootcamp.shelter.service.data.ShelterReportData;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ShelterAnalyticsService {

    public ShelterReportData buildReportData(ImportResult importResult) {
        List<Animal> allAnimals = importResult.allAnimals(); // from import

        Set<String> uniqueSpecies = new TreeSet<>(); // For info -> TreeSet sorts alphabetically
        Map<String, List<Animal>> animalsBySpecies = new HashMap<>();
        List<String> animalsNeedingVetInput = new ArrayList<>();

        // TODO Step 2 Done:

        for (Animal animal : allAnimals)
        {
            // - sort animals By Species
            uniqueSpecies.add(animal.getSpecies()); // is a Set so remove duplicates
            //group animal by species
            animalsBySpecies.computeIfAbsent(animal.getSpecies(), species -> new ArrayList<>()).add(animal);

            // - animalsNeedingVetInput with format name(species)
            if (!animal.isVaccinated())
            {
                animalsNeedingVetInput.add(animal.getName() + "(" + animal.getSpecies() + ")");
            }
        }

        // TODO Step 3: Done
        // vaccinated counts per species via stream
        Map<String, Long> vaccinatedCountBySpecies = allAnimals.stream()
                .filter(Animal::isVaccinated)
                .collect(Collectors.groupingBy(Animal::getSpecies, Collectors.counting()));

        // unvaccinated counts per species via stream
        Map<String, Long> unvaccinatedCountBySpecies = allAnimals.stream()
                .filter(animal -> !animal.isVaccinated())
                .collect(Collectors.groupingBy(Animal::getSpecies, Collectors.counting()));

        // oldest animal per species, excluding unknown ages via stream
        Map<String, Animal> oldestAnimalBySpecies = allAnimals.stream()
                .filter(animal -> animal.getAge() != null)
                .collect(Collectors.toMap(Animal::getSpecies, Function.identity(), BinaryOperator.maxBy(Comparator.comparing(Animal::getAge)))); // ok mb one line is a bit too much :)

        return new ShelterReportData(importResult, uniqueSpecies, animalsBySpecies, animalsNeedingVetInput, vaccinatedCountBySpecies, unvaccinatedCountBySpecies, oldestAnimalBySpecies); // ok mb one line is a bit too much :) :)
    }
}
