package ticket.booking.services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ticket.booking.entites.Train;
public class TrainService{

    private List<Train> trainList;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String TRAIN_DB_PATH = "/trains.json";

    public TrainService() throws IOException {
        File trains = new File(TRAIN_DB_PATH);
        trainList = objectMapper.readValue(trains, new TypeReference<List<Train>>(){}) ;
    }   

    
    public List<Train> searchTrain(String source , String destination) {
        return trainList.stream().filter(train-> validTrain(train,source,destination)).collect(Collectors.toList()) ;
    }

    
    public void addTrain(Train newTrain){
        Optional<Train> isTrain = trainList.stream().filter(train -> train.getTrainId().equalsIgnoreCase(newTrain.getTrainId())).findFirst();
        if(isTrain.isPresent()){
            updateTrain(newTrain);
        }
        else{
            trainList.add(newTrain);
            saveTrainListToFile();
        }
    }

    
    public void updateTrain(Train updatedTrain){    
        OptionalInt index = IntStream.range(0, trainList.size())
                            .filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(updatedTrain.getTrainId()))
                            .findFirst();

        if(index.isPresent()){
            trainList.set(index.getAsInt(), updatedTrain);
            saveTrainListToFile();
        }
        else{
            addTrain(updatedTrain);
        }
    }
    
    
    public void saveTrainListToFile() {
        try {
            objectMapper.writeValue(new File(TRAIN_DB_PATH), trainList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public boolean validTrain(Train train, String source, String destination){
        List<String> stations = train.getStations();

        int srcIndex = stations.indexOf(source.toLowerCase());
        int dstIndex = stations.indexOf(destination.toUpperCase());

        return srcIndex!=-1 && dstIndex!=-1 && srcIndex<dstIndex;
    }
}