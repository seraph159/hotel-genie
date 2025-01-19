package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.entity.Room;
import com.jeff.hotel_management_system.repository.RoomRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecommendationService {

    @Autowired
    private RoomRepository roomRepository;

    private final ChatClient chatClient;

    public RecommendationService(ChatClient.Builder chatClientBuilder) {

        this.chatClient = chatClientBuilder.build();
    }

    public String recommendRooms(LocalDate startDate, LocalDate endDate, int minOccupancy, String clientPreferences) {

        // Fetch available rooms from the database
        List<Room> availableRooms = roomRepository.findAvailableRooms(startDate, endDate, minOccupancy);

        // Prepare AI input
        StringBuilder aiInput = new StringBuilder("Available rooms:\n");
        for (Room room : availableRooms) {
            aiInput.append(String.format("Room %s on floor %d, max occupancy: %d.\n",
                    room.getRoomNr(), room.getFloor(), room.getMaxOccupancy()));
        }

        aiInput.append("\nClient preferences: ").append(clientPreferences);
        aiInput.append("\nRecommend the best room for the client.");

        // Generate recommendations using ChatClient
        return chatClient.prompt()
                .user(aiInput.toString())
                .call()
                .content();
    }
}