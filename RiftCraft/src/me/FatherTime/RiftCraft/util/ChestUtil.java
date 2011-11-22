package me.FatherTime.RiftCraft.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Coen Meulenkamp (Scriblon, ~theJaf) <coenmeulenkamp at gmail.com>
 */
public class ChestUtil {
    /**
     * Credit goes to bergerkiller http://forums.bukkit.org/members/bergerkiller.96957/
     * @param inv
     * @param item 
     */
    public static void removeInventoryItems(Inventory inv, ItemStack item) {
        removeInventoryItems(inv, item.getType(), item.getAmount());
    }
    
    /**
     * Credit goes to bergerkiller http://forums.bukkit.org/members/bergerkiller.96957/
     * @param inv
     * @param type
     * @param amount 
     */
    public static void removeInventoryItems(Inventory inv, Material type, int amount) {
        for (ItemStack is : inv.getContents()) {
            if (is != null && is.getType() == type) {
                int newamount = is.getAmount() - amount;
                if (newamount > 0) {
                    is.setAmount(newamount);
                    break;
                } else {
                    inv.remove(is);
                    amount = -newamount;
                    if (amount == 0) break;
                }
            }
        }
    }
    
    public static boolean hasInventoryReqContent(Inventory inv, ItemStack item){
        return hasInventoryReqContent(inv, item.getType(), item.getAmount());
    }
    
    public static boolean hasInventoryReqContent(Inventory inv, Material type, int amount){
        int total = 0;
        for(ItemStack is : inv.getContents()){
            if (is != null && is.getType() == type) {
                total = +is.getAmount();
            }
            if(total >= amount){
                return true;
            }
        }
        return false;
    }
}
