package lab6.server;

import lab6.common.Response;
import lab6.common.requests.*;
import lab6.common.models.City;
import lab6.server.managers.CollectionManager;
import lab6.server.managers.FileManager;
import lab6.common.requests.UnknownCommandRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CommandExecutor {
    private final CollectionManager collectionManager;
    private final FileManager fileManager;

    public CommandExecutor(CollectionManager collectionManager, FileManager fileManager) {
        this.collectionManager = collectionManager;
        this.fileManager = fileManager;
    }

    public Response execute(CommandRequest request) {
        if (request instanceof UnknownCommandRequest){ return new Response(false, "Нет такой команды, бебебе.\nВведите help.", null);}
        if (request instanceof AddRequest) return handleAdd((AddRequest) request);
        if (request instanceof UpdateRequest) return handleUpdate((UpdateRequest) request);
        if (request instanceof RemoveByIdRequest) return handleRemoveById((RemoveByIdRequest) request);
        if (request instanceof RemoveAnyByCarCodeRequest) return handleRemoveAnyByCarCode((RemoveAnyByCarCodeRequest) request);
        if (request instanceof RemoveLowerRequest) return handleRemoveLower((RemoveLowerRequest) request);
        if (request instanceof AddIfMaxRequest) return handleAddIfMax((AddIfMaxRequest) request);
        if (request instanceof ClearRequest) return handleClear();
        if (request instanceof InfoRequest) return handleInfo();
        if (request instanceof ShowRequest) return handleShow();
        if (request instanceof MinByCoordinatesRequest) return handleMinByCoordinates();
        if (request instanceof PrintFieldDescendingMetersAboveSeaLevelRequest)
            return handlePrintFieldDescendingMetersAboveSeaLevel();
        return new Response(false, "Неизвестная команда", null);
    }

    private void saveCollection() {
        try {
            fileManager.save(collectionManager.getCities());
        } catch (Exception e) {
            System.err.println("Ошибка автосохранения: " + e.getMessage());
        }
    }

    private Response handleAdd(AddRequest req) {
        City city = req.getCity();
        city.setId(collectionManager.generateId());
        city.setCreationDate(LocalDate.now());
        collectionManager.addCity(city);
        saveCollection();
        return new Response(true, "Город добавлен с id=" + city.getId(), null);
    }

    private Response handleUpdate(UpdateRequest req) {
        if (!collectionManager.containsId(req.getId())) {
            return new Response(false, "Элемент с id=" + req.getId() + " не найден.", null);
        }
        City oldCity = collectionManager.getById(req.getId()).get();
        City newData = req.getCity();
        City updatedCity = new City(
                oldCity.getId(),
                newData.getName(),
                newData.getCoordinates(),
                oldCity.getCreationDate(),
                newData.getArea(),
                newData.getPopulation(),
                newData.getMetersAboveSeaLevel(),
                newData.getCarCode(),
                newData.getClimate(),
                newData.getStandardOfLiving(),
                newData.getGovernor()
        );
        collectionManager.removeById(req.getId());
        collectionManager.addCity(updatedCity);
        saveCollection();
        return new Response(true, "Город с id=" + req.getId() + " обновлён.", null);
    }

    private Response handleRemoveById(RemoveByIdRequest req) {
        if (!collectionManager.containsId(req.getId())) {
            return new Response(false, "Элемент с id=" + req.getId() + " не найден.", null);
        }
        collectionManager.removeById(req.getId());
        saveCollection();
        return new Response(true, "Элемент с id=" + req.getId() + " удалён.", null);
    }

    private Response handleRemoveAnyByCarCode(RemoveAnyByCarCodeRequest req) {
        var city = collectionManager.getCities().stream()
                .filter(c -> c.getCarCode() == req.getCarCode())
                .findAny();
        if (city.isPresent()) {
            collectionManager.removeCity(city.get());
            saveCollection();
            return new Response(true, "Удалён город с id=" + city.get().getId(), null);
        } else {
            return new Response(false, "Город с carCode=" + req.getCarCode() + " не найден.", null);
        }
    }

    private Response handleRemoveLower(RemoveLowerRequest req) {
        int initialSize = collectionManager.size();
        collectionManager.getCities().removeIf(city -> city.compareTo(req.getReference()) < 0);
        int removed = initialSize - collectionManager.size();
        if (removed > 0) saveCollection();
        return new Response(true, "Удалено элементов: " + removed, null);
    }

    private Response handleAddIfMax(AddIfMaxRequest req) {
        City newCity = req.getCity();
        boolean isMax = collectionManager.getMaxCity()
                .map(max -> newCity.compareTo(max) > 0)
                .orElse(true);
        if (isMax) {
            newCity.setId(collectionManager.generateId());
            newCity.setCreationDate(LocalDate.now());
            collectionManager.addCity(newCity);
            saveCollection();
            return new Response(true, "Город добавлен с id=" + newCity.getId(), null);
        } else {
            return new Response(false, "Город не превышает максимальный. Добавление отменено.", null);
        }
    }

    private Response handleClear() {
        collectionManager.clear();
        saveCollection();
        return new Response(true, "Коллекция очищена.", null);
    }

    private Response handleInfo() {
        String info = "Тип коллекции: " + collectionManager.getCollectionType() +
                "\nДата инициализации: " + collectionManager.getInitializationDate() +
                "\nКоличество элементов: " + collectionManager.size();
        return new Response(true, info, null);
    }

    private Response handleShow() {
        if (collectionManager.size() == 0) {
            return new Response(true, "Коллекция пуста.", null);
        }
        List<City> sorted = collectionManager.getCities().stream()
                .sorted()
                .collect(Collectors.toList());
        return new Response(true, "Элементы коллекции:", sorted);
    }

    private Response handleMinByCoordinates() {
        return collectionManager.getMinByCoordinates()
                .map(city -> new Response(true, "Город с минимальной координатой X:", city))
                .orElse(new Response(false, "Коллекция пуста.", null));
    }

    private Response handlePrintFieldDescendingMetersAboveSeaLevel() {
        List<Integer> heights = collectionManager.getMetersAboveSeaLevelDescending();
        if (heights.isEmpty()) {
            return new Response(true, "Нет городов с указанной высотой (или коллекция пуста).", null);
        }
        return new Response(true, "Высоты в порядке убывания:", heights);
    }
}