/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.dto;

import java.util.List;
import java.util.Map;

/**
 *
 * @author rajeshk
 */
public class WeeksTimeslots {
    
    Map<String, String> dateMap;
    
    
    List<Timeslot> timeslots;

    public Map<String, String> getDateMap() {
        return dateMap;
    }

    public void setDateMap(Map<String, String> dateMap) {
        this.dateMap = dateMap;
    }

    public List<Timeslot> getTimeslots() {
        return timeslots;
    }

    public void setTimeslots(List<Timeslot> timeslots) {
        this.timeslots = timeslots;
    }
    
    public static class Timeslot {
        
        String time;
        
        List<Count> counts;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public List<Count> getCounts() {
            return counts;
        }

        public void setCounts(List<Count> counts) {
            this.counts = counts;
        }
        
        public static class Count {
            
            int availabilityCount;
            
            int bookedCount;

            public int getAvailabilityCount() {
                return availabilityCount;
            }

            public void setAvailabilityCount(int availabilityCount) {
                this.availabilityCount = availabilityCount;
            }

            public int getBookedCount() {
                return bookedCount;
            }

            public void setBookedCount(int bookedCount) {
                this.bookedCount = bookedCount;
            }
            
        }
        
    }
    
}
