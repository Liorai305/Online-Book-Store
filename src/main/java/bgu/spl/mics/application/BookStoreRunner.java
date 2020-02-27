package bgu.spl.mics.application;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

import bgu.spl.mics.InitializationCount;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;


/** This is the Main class of the application.
 *
 */
public class BookStoreRunner {
    public static void main(String[] args) {
            // read the json file
            Gson gson = new Gson();
            try{
                BufferedReader reader = new BufferedReader(new FileReader(args[0]));
               HashMap settings = gson.fromJson(reader, HashMap.class);

               //creating array of BookInventoryInfo
                ArrayList initialInventory = (ArrayList)settings.get("initialInventory");
                Iterator iter=initialInventory.iterator();
                BookInventoryInfo[] books=new BookInventoryInfo[initialInventory.size()];
                int i=0;
                while(iter.hasNext()){
                    LinkedTreeMap book=(LinkedTreeMap)iter.next();
                    BookInventoryInfo temp= new BookInventoryInfo((String)book.get("bookTitle"), ((Double)book.get("amount")).intValue(), ((Double)book.get("price")).intValue());
                    books[i]=temp;
                    i++;
                }
                //initializing Inventory
                Inventory inventory=Inventory.getInstance();
                inventory.load(books);

                ArrayList initialResources = (ArrayList)settings.get("initialResources");
                LinkedTreeMap resources=(LinkedTreeMap)initialResources.get(0);
                ArrayList to_vehicles=(ArrayList)resources.get("vehicles");
                Iterator iter2=to_vehicles.iterator();
                DeliveryVehicle[] vehicles=new DeliveryVehicle[to_vehicles.size()];
                int j=0;
                //creating array of DeliveryVehicles
                while(iter2.hasNext()){
                    LinkedTreeMap vehicle=(LinkedTreeMap)iter2.next();
                    DeliveryVehicle temp= new DeliveryVehicle(((Double)vehicle.get("license")).intValue(), ((Double)vehicle.get("speed")).intValue());
                    vehicles[j]=temp;
                    j++;
                }
                //initializes ResourcesHolder
                ResourcesHolder resourcesHolder=ResourcesHolder.getInstance();
                resourcesHolder.load(vehicles);
                LinkedTreeMap services= (LinkedTreeMap) settings.get("services");
                LinkedTreeMap time=(LinkedTreeMap) services.get("time");


                //Initialize Time Service
                TimeService timeService=new TimeService(((Double)time.get("speed")).intValue(),((Double)time.get("duration")).intValue());
                Thread time_thread=new Thread(timeService);

                //Initialize Selling services
                int count1=((Double)services.get("selling")).intValue();
                Thread[] sellingArray=new Thread[count1];
                for(int n=0; n<count1;n++) {
                    SellingService sellingService = new SellingService("SellingService" + n);
                    sellingArray[n] = new Thread(sellingService);
                    sellingArray[n].start();
                }

                //Initialize Inventory Services
                int count2=((Double)services.get("inventoryService")).intValue();
                Thread[] InventoryArray=new Thread[count2];
                for(int m=0; m<count2;m++) {
                    InventoryService inventoryService = new InventoryService("InventoryService" + m);
                    InventoryArray[m] = new Thread(inventoryService);
                    InventoryArray[m].start();
                }

                //Initialize Logistics services
                int count3=((Double)services.get("logistics")).intValue();
                Thread[] LogisticArray=new Thread[count3];
                for(int k=0; k<count3;k++) {
                    LogisticsService logisticsService = new LogisticsService("LogisticsService" + k);
                    LogisticArray[k] = new Thread(logisticsService);
                    LogisticArray[k].start();
                }

                //initialize Resources services
                int count4=((Double)services.get("resourcesService")).intValue();
                Thread[] ResourcesArray=new Thread[count4];
                for(int l=0; l<count4;l++) {
                    ResourceService resourceService = new ResourceService("ResourceService" + l);
                    ResourcesArray[l] = new Thread(resourceService);
                    ResourcesArray[l].start();
                }

                //creating customers
                ArrayList customers=(ArrayList)services.get("customers");
                Iterator iter3=  customers.iterator();
                int api_counter=0;
                HashMap<Integer,Customer> customerIntegerHashMap=new HashMap<>();
                Thread[] APIServicesArray=new Thread[customers.size()];
                while (iter3.hasNext()){
                    //for each customer
                    LinkedTreeMap to_customer=(LinkedTreeMap)iter3.next();
                    Customer customer=new Customer((String)to_customer.get("name"), ((Double)to_customer.get("id")).intValue(),(String)to_customer.get("address"),
                            ((Double)to_customer.get("distance")).intValue(),((Double)((LinkedTreeMap)to_customer.get("creditCard")).get("amount")).intValue()
                            ,((Double)((LinkedTreeMap)to_customer.get("creditCard")).get("number")).intValue());

                    ArrayList to_orderSchedule=(ArrayList)to_customer.get("orderSchedule");

                    //creates the HashMap of customers for the output
                    customerIntegerHashMap.put( ((Double)to_customer.get("id")).intValue(),customer);
                    LinkedBlockingQueue orderSchedule=new LinkedBlockingQueue();
                    Iterator iter4= to_orderSchedule.iterator();

                    //creates an array of BookOrderEvent for each customer
                    while (iter4.hasNext()){
                        LinkedTreeMap to_order=(LinkedTreeMap)iter4.next();
                        BookOrderEvent bookOrderEvent= new BookOrderEvent((String)to_order.get("bookTitle"), customer,((Double)to_order.get("tick")).intValue());
                        orderSchedule.add(bookOrderEvent);
                    }

                    //creates an APIService for each customer
                    APIService apiService=new APIService("APIService"+api_counter,orderSchedule,customer);
                    APIServicesArray [api_counter]=new Thread(apiService);
                    APIServicesArray [api_counter].start();
                    api_counter++;
                }
                //timeToStartCount = the amount of services in the store
                int timeToStartCount=customers.size()+count4+count3+count2+count1;
                //waits to start the time service until all the other services initializes
                while(InitializationCount.getInstance().getCount()<timeToStartCount);
                time_thread.start();

                //waits for all the threads to terminate in order to create output files
                try {

                    for (int z=0; z<count1; z++){
                        sellingArray[z].join();
                    }
                    for (int z=0; z<count2; z++){
                        InventoryArray[z].join();
                    }
                    for (int z=0; z<count3; z++){
                        LogisticArray[z].join();
                    }
                    for (int z=0; z<count4; z++){
                        ResourcesArray[z].join();
                    }
                    for (int z=0; z<customers.size(); z++){
                        APIServicesArray[z].join();
                    }
                    time_thread.join();
                }

                catch (InterruptedException e) {}


                MoneyRegister moneyRegister=MoneyRegister.getInstance();


            try {
                //print customers
                FileOutputStream file1 = new FileOutputStream(args[1]);
                ObjectOutputStream out1 = new ObjectOutputStream(file1);
                out1.writeObject(customerIntegerHashMap);
                out1.close();
                file1.close();

                //print moneyRegister
                FileOutputStream file2 = new FileOutputStream(args[4]);
                ObjectOutputStream out2 = new ObjectOutputStream(file2);
                out2.writeObject(moneyRegister);
                out2.close();
               file2.close();

            }
            catch (IOException e) {}
            //prints inventory
                inventory.printInventoryToFile(args[2]);
            //prints money register receipt
                moneyRegister.printOrderReceipts(args[3]);

            }
            catch (IOException e){

            }


    }

}
