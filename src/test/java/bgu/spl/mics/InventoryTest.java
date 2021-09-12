package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Inventory;//dt
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;//dt
import org.junit.jupiter.api.Test;//dt


import java.io.File;
import java.io.IOException;
//import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;//dt

import static org.junit.jupiter.api.Assertions.*;



public class InventoryTest {
    private Inventory inv;

    @BeforeEach
    public void setUp(){
        try{
            this.inv = Inventory.getInstance();
        }
        catch (Exception e){
            fail("The setUp method could't creat a new instance of Inventory" + e.getMessage());
        }
    }

    @Test
    public void testgetInstance(){
        Inventory cinv1 = Inventory.getInstance();
        Inventory cinv2 = Inventory.getInstance();

        assertNotNull(cinv1);
        assertNotNull(cinv2);

        assertTrue(cinv1 == cinv2);

        inv = (Inventory)cinv1;
    }

    //a helper function which gives an array of gadgets
    private String [] getGadgetArr(){
        String[] gdArray = new String[4];
          gdArray[0] = "gadget1";
          gdArray[1] = "gadget2";
          gdArray[2] = "gadget3";
          gdArray[3] = "gadget4";
      return  gdArray;
    };

    @Test
    public void testload(){
        try{
            String[] gdArr = getGadgetArr();
            int size = gdArr.length;
            inv.load(gdArr);
        }
        catch (Exception e){
            fail("Some items did't get loaded" + e.getMessage());
        }

    }

    @Test
    //checking if the method does return false if the item is missing, and true otherwise
    public void testgetItem(){
         try{
             assertFalse(inv.getItem(" "));
             String[] gdArr = getGadgetArr();
             int size = gdArr.length;
             inv.load(gdArr);

             boolean loaded = true;
             for(int i =0; i < size-1 ; i++){
                 if(! (inv.getItem(gdArr[i])) ) {
                     loaded = false;
                     break;
                 }
             }
             assertTrue(loaded);
         }
        catch (Exception e){
          fail("the method didn't recognize an existent item or did't recognize it is missing  " + e.getMessage());
        }
    }

    //checking if the method removes a gadget after it's been used
    public void testgetItem2(){
        try{
            String[] gdArr = getGadgetArr();
            inv.load(gdArr);
             assertTrue(inv.getItem("gadget1"));
             assertFalse(inv.getItem("gadget1"));
            assertTrue(inv.getItem("gadget4"));
            assertFalse(inv.getItem("gadget4"));
        }
        catch (Exception e){
            fail("" + e.getMessage());
        }
    }

    @Test
    public void testprintToFile(){
        try {
            String[] gdArr = getGadgetArr();
            inv.load(gdArr);
            inv.printToFile("a.json");
            File temp = new File("a.json");
            assertTrue(temp.exists());

            String data = "";
            data = new String(Files.readAllBytes(Paths.get("a.json")));
            Gson gson = new Gson();
            List<String> obj2 = gson.fromJson(data, List.class);
            for(int i = 0 ; i < gdArr.length ; i ++){
                assertTrue(obj2.contains(gdArr[i]));
            }
        }
        catch (Exception e){
            fail("Unexpected exception" + e.getMessage());
        }
    }
}
