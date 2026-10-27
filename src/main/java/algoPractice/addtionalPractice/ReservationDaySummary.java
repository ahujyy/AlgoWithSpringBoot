package algoPractice.addtionalPractice;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;



/**

### **Anomaly detection**

**Description**

We want to create an algorithm for detecting days with a lower number of active reservations. 
Lets represent dates as non-negative integers and hotel room reservation as struct 


{
    reservation_id int
    checkin_date int
    checkout_date int
}



Reservation is active on a specific date if it's within checkin and checkout date (excluding).
We want to detect all the dates that have a number of active reservations less than 80% of the previous 7 days average.

**Example :**
**Input:**
Reservations:
(123, 0, 6) `reservation_id = 123, checkin = 0, checkout = 6`
(435, 3, 7)
(444, 2, 13)
(532, 5, 9)
(232, 9, 12)
(221, 10, 12)

**Output:**
[8, 9, 12, 13]

 * ReservationDaySummary
 */

public class ReservationDaySummary {
    public record Reserv(int reservId, int checkIn , int checkOut){}

    public static void main(String[] args) {
        List<Reserv> data = new ArrayList<Reserv>();

        data.add(new Reserv(123, 0, 6));
        data.add(new Reserv(435, 3, 7));
        data.add(new Reserv(444, 2, 13));
        data.add(new Reserv(532, 5, 9));
        data.add(new Reserv(232, 9, 12));
        data.add(new Reserv(221, 10, 12));

        ReservationDaySummary ob = new ReservationDaySummary();
        List<Integer> rList = ob.getAbnormalDays(data, 7, 0.8);
        System.out.println(rList);

    }

    //8.9.12.13
    public List<Integer> getAbnormalDays(List<Reserv> data, int lastDays, double threshold){

        //data.stream().mapToInt(Reserv::checkIn).min().orElse(0);

        data.sort(Comparator.comparing(Reserv::checkIn));
        if(data.isEmpty())return List.of();

        var map = new HashMap<Integer, Integer>();
        int startD = data.get(0).checkIn() , endD = 0;

        
        for(Reserv reserv : data){
            if(reserv.checkOut() > endD )endD = reserv.checkOut();
            for(int i = reserv.checkIn(); i < reserv.checkOut(); i++){
                map.compute(i, (k,ov)-> ov == null ? 1 : ov+1 );
            }
        }

        var results = new ArrayList<Integer>();
        var previous = new HashMap<Integer, Integer>();
        for(int i= startD; i<=endD; i++){
           
            int total = previous.computeIfAbsent(i-1, k->0);
            int sum = total + map.computeIfAbsent(i -1 , k->0) - map.computeIfAbsent(i - lastDays -1, k->0);
            previous.put(i, sum);

            int tVal = map.computeIfAbsent(i, k->0);
            if(tVal < (sum * threshold/7) ){
                System.out.println("date: " + i+ ", val: " +tVal +
                ", previous " + lastDays + " days sum: " + sum +
                 ", 0.8*avg: " + (sum * threshold/7) 
                );
                results.add(i);
            }
            
        }
        return results;
        
    }

}
