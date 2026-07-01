package lv.bootcamp.shelter.service.data;
import java.util.Map;
import lv.bootcamp.shelter.model.Animal;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

//I guess It could be a record instead of Regular class  ?
public class ShelterReportData
{
    private final ImportResult importResult;
    private final Set<String> uniqueSpecies;
    private final Map<String, List<Animal>> animalsBySpecies;
    private final List<String> animalsNeedingVetInput;
    private final Map<String, Long> vaccinatedCountBySpecies;
    private final Map<String, Long> unvaccinatedCountBySpecies;
    private final Map<String, Animal> oldestAnimalBySpecies;
}


