package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ReportExportService {

    public void writeReport(Path outputPath, ShelterReportData reportData) {
        // TODO Step 4:
        try {
            Path parent = outputPath.getParent();

            if (parent != null)
            {
                Files.createDirectories(parent);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8))  // 6) Use UTF-8 and try-with-resources.
            {
                // 1) Write upload-report.txt in required format.
                // 2) Include generated date, imported/skipped totals.
                writer.write("Shelter Report");
                writer.newLine();

                writer.write("Generated at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.newLine();
                writer.newLine();

                writer.write("Total imported: " + reportData.getImportResult().allAnimals().size());
                writer.newLine();

                writer.write("Total skipped: " + reportData.getImportResult().skippedRows());
                writer.newLine();
                writer.newLine();

                // 3) Include unique species and per-species breakdown.
                    //Unique species:
                writer.write("Unique species: " + String.join(", ", reportData.getUniqueSpecies()));
                writer.newLine();
                writer.newLine();

                    //Unique species breakdown
                writer.write("Per-species breakdown:");
                writer.newLine();

                for (String species : reportData.getUniqueSpecies())
                {
                    List<Animal> animals = reportData.getAnimalsBySpecies().getOrDefault(species, List.of());
                    long vaccinated = reportData.getVaccinatedCountBySpecies().getOrDefault(species, 0L); //info for me getOrDefault - just to avoid null
                    long unvaccinated = reportData.getUnvaccinatedCountBySpecies().getOrDefault(species, 0L);

                    writer.write("- " + species + ": total=" + animals.size() + ", vaccinated=" + vaccinated + ", unvaccinated=" + unvaccinated);
                    writer.newLine();
                }

                writer.newLine();

                // 4) Include oldest animal per species.
                writer.write("Oldest animal per species:");
                writer.newLine();

                for (Map.Entry<String, Animal> entry : reportData.getOldestAnimalBySpecies().entrySet())
                {
                    String species = entry.getKey();
                    Animal animal = entry.getValue();

                    writer.write("- " + species + ": " + animal.getName() + " (" + animal.getAge() + " years)");
                    writer.newLine();
                }

                writer.newLine();

                // 5) Include animalsNeedingVetInput as name(species), name2(species2).
                writer.write("Animals needing vet input: ");

                if (reportData.getAnimalsNeedingVetInput().isEmpty())
                {
                    writer.write("None");
                }
                else
                {
                    writer.write(String.join(", ", reportData.getAnimalsNeedingVetInput()));
                }

                writer.newLine();
            }

        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to write report", e);
        }
    }
}