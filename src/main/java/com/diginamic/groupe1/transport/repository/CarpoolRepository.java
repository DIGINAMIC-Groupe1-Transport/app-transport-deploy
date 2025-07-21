package com.diginamic.groupe1.transport.repository;

import com.diginamic.groupe1.transport.entity.Carpool;
import com.diginamic.groupe1.transport.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CarpoolRepository extends JpaRepository<Carpool, Long> {

    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
        FROM Carpool c
        WHERE c.organizer.id = :organizerId
          AND :newStart < c.estimatedArrivalTime
          AND :newEnd > c.estimatedDepartureTime
    """)
    boolean existsOverlapingCarpool(@Param("organizerId") Long organizerId,
                                     @Param("newStart") LocalDateTime newStart,
                                     @Param("newEnd") LocalDateTime newEnd);

    boolean existsByIdAndOrganizerId(Long id, Long organizerId);

    Page <Carpool> findAll (Pageable pageable);

    Optional <Carpool> findById (Long id);

    Page<Carpool> findByOrganizerId (Long organizerId, Pageable pageable);

    Page<Carpool> findByParticipantsId (Long participantsId, Pageable pageable);

    @Query("""
    SELECT COUNT(c) > 0 FROM Carpool c
    WHERE c.vehicle.id = :vehicleId
    AND c.isCanceled = false
    AND c.estimatedArrivalTime > :now
    AND size(c.participants) > 0
""")
    boolean existsActiveCarpoolsWithParticipantsUsingVehicle(
            @Param("vehicleId") Long vehicleId,
            @Param("now") LocalDateTime now
    );

    @Query(value = """
    SELECT
           c.id,
           c.creation_time,
           c.departure_date,
           c.estimated_departure_time,
           c.estimated_arrival_time,
           c.estimated_duration,
           c.estimated_length,
           c.initial_available_seats - COALESCE(COUNT(ucp.fk_user_participant_id), 0) AS remaining_seats,
           c.is_canceled,
           
           start_coord.address AS start_address,
           start_coord.city AS start_city,
           start_coord.street_name AS start_street_name,
           start_coord.house_number AS start_house_number,
           start_coord.long_coordinates AS start_longitude,
           start_coord.lat_coordinates AS start_latitude,
           
           end_coord.address AS end_address,
           end_coord.city AS end_city,
           end_coord.street_name AS end_street_name,
           end_coord.house_number AS end_house_number,
           end_coord.long_coordinates AS end_longitude,
           end_coord.lat_coordinates AS end_latitude,
           
           organizer.first_name AS organizer_first_name,
           organizer.last_name AS organizer_last_name,

           vehicle.model AS model,
           
           (6371000 * ACOS(GREATEST(-1, LEAST(1,
               COS(RADIANS(start_coord.lat_coordinates)) * 
               COS(RADIANS(:startY)) * 
               COS(RADIANS(start_coord.long_coordinates) - RADIANS(:startX)) + 
               SIN(RADIANS(start_coord.lat_coordinates)) * 
               SIN(RADIANS(:startY))
           )))) AS depart_distance,

           (6371000 * ACOS(GREATEST(-1, LEAST(1,
               COS(RADIANS(end_coord.lat_coordinates)) * 
               COS(RADIANS(:endY)) * 
               COS(RADIANS(end_coord.long_coordinates) - RADIANS(:endX)) + 
               SIN(RADIANS(end_coord.lat_coordinates)) * 
               SIN(RADIANS(:endY))
           )))) AS arrive_distance,

           (
               (6371000 * ACOS(GREATEST(-1, LEAST(1,
                   COS(RADIANS(start_coord.lat_coordinates)) * 
                   COS(RADIANS(:startY)) * 
                   COS(RADIANS(start_coord.long_coordinates) - RADIANS(:startX)) + 
                   SIN(RADIANS(start_coord.lat_coordinates)) * 
                   SIN(RADIANS(:startY))
               )))) * :startWeight +
               (6371000 * ACOS(GREATEST(-1, LEAST(1,
                   COS(RADIANS(end_coord.lat_coordinates)) * 
                   COS(RADIANS(:endY)) * 
                   COS(RADIANS(end_coord.long_coordinates) - RADIANS(:endX)) + 
                   SIN(RADIANS(end_coord.lat_coordinates)) * 
                   SIN(RADIANS(:endY))
               )))) * :endWeight
           ) AS weighted_distance

       FROM carpools c
       JOIN coordinates start_coord ON c.fk_coordinates_start_id = start_coord.id
       JOIN coordinates end_coord ON c.fk_coordinates_end_id = end_coord.id
       JOIN users organizer ON c.fk_user_organizer_id = organizer.id
       JOIN vehicles vehicle ON c.fk_vehicle_id = vehicle.id
       LEFT JOIN users_carpools_participants ucp ON c.id = ucp.fk_carpool_id

       WHERE (:departureDate IS NULL OR c.departure_date = :departureDate)
         AND c.is_canceled = false
         AND c.estimated_departure_time > :currentDateTime

       GROUP BY c.id, c.creation_time, c.departure_date, c.estimated_departure_time, 
                c.estimated_arrival_time, c.estimated_length, c.estimated_duration, 
                c.initial_available_seats, c.is_canceled,
                start_coord.address, start_coord.city, start_coord.street_name, 
                start_coord.house_number, start_coord.long_coordinates, start_coord.lat_coordinates,
                end_coord.address, end_coord.city, end_coord.street_name, 
                end_coord.house_number, end_coord.long_coordinates, end_coord.lat_coordinates, 
                organizer.first_name, organizer.last_name, vehicle.model

       ORDER BY weighted_distance ASC
       LIMIT :limit OFFSET :offset
""", nativeQuery = true)
    List<Object[]> findMatchingCarpools(
            @Param("startX") double startX,
            @Param("startY") double startY,
            @Param("endX") double endX,
            @Param("endY") double endY,
            @Param("departureDate") LocalDate departureDate,
            @Param("currentDateTime") LocalDateTime currentDateTime,
            @Param("startWeight") double startWeight,
            @Param("endWeight") double endWeight,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
    SELECT COUNT(DISTINCT c.id)
    FROM carpools c
    JOIN coordinates start_coord ON c.fk_coordinates_start_id = start_coord.id
    JOIN coordinates end_coord ON c.fk_coordinates_end_id = end_coord.id
    LEFT JOIN users_carpools_participants ucp ON c.id = ucp.fk_carpool_id
    WHERE (:departureDate IS NULL OR c.departure_date = :departureDate)
      AND c.is_canceled = false
      AND c.estimated_departure_time > :currentDateTime
    """, nativeQuery = true)
    Long countMatchingCarpools(
            @Param("startX") double startX,
            @Param("startY") double startY,
            @Param("endX") double endX,
            @Param("endY") double endY,
            @Param("departureDate") LocalDate departureDate,
            @Param("currentDateTime") LocalDateTime currentDateTime
    );

}