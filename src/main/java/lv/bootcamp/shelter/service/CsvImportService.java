package lv.bootcamp.shelter.service;

import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;

import java.io.BufferedReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

@Slf4j
public class CsvImportService
{
    public ImportResult importAnimals(Path inputPath)
    {
        List<Animal> allAnimals = new ArrayList<>();
        int skippedRows = 0;

        log.info("Starting import from {}", inputPath);

        // 1) Read intake.csv with UTF-8 via path provided from main
        try (BufferedReader br = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)) {

            // // 2) Skip header row.
            br.readLine();

            //read Lines
            String line;
            while ((line = br.readLine()) != null)
            {
                try
                {
                    // 3) Skip malformed rows and log warnings.
                    String[] parts = line.split(",");

                    if (parts.length != 5)
                    {
                        log.warn("Malformed row (wrong columns): {}", line);
                        skippedRows++;
                        continue;
                    }

                    //get name and species
                    String name = parts[0].trim();
                    String species = parts[1].trim();

                    //remove empty species not required but look weired in report
                    if (name.isBlank() || species.isBlank())
                    {
                        log.warn("Missing required name or species, skipping row: {}", line);
                        skippedRows++;
                        continue;
                    }

                    // 4) Allow blank age as unknown (null), but reject non-numeric age values.
                    Integer age = null;
                    String ageRaw = parts[2].trim();

                    if (!ageRaw.isEmpty()) // try to allow blank age as unknown (null)
                    {
                        try
                        {
                            age = Integer.parseInt(ageRaw);
                        }
                        catch (NumberFormatException e) // but reject non-numeric age values.
                        {
                            log.warn("Non-numeric age, skipping row: {}", line);
                            skippedRows++;
                            continue;
                        }
                    }

                    //get vaccination state
                    boolean vaccinated = Boolean.parseBoolean(parts[3].trim());

                    // 5) Parse intakeDate using DateTimeFormatter. get admission data
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate intakeDate = LocalDate.parse(parts[4].trim(), formatter);

                    // 6) Map each row to Animal object.
                    Animal animal = new Animal(name, species, age, vaccinated, intakeDate);
                    allAnimals.add(animal);

                }
                catch (Exception e)
                {
                    log.warn("Skipping malformed row: {}", line);
                    skippedRows++;
                }
            }

        } catch (IOException e) {
            log.error("Failed to read file", e);
        }

        return new ImportResult(allAnimals, skippedRows);
    }
}
