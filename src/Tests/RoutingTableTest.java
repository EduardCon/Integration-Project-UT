package Tests;

import ApplicationLayer.Client;
import ApplicationLayer.RoutingTable;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class RoutingTableTest {

    private RoutingTable routingTable;

    @Before
    public void setUp(){
        this.routingTable = new RoutingTable(new Client("Test", 9001));
    }

    @Test
    public void testCreateObject(){
        assertTrue(this.routingTable.getClient() != null);
    }

    @Test
    public void testTableEntry() {
        Map<Integer, List<RoutingTable.TableEntry>> testMap = new HashMap<>();
        RoutingTable.TableEntry tb = new RoutingTable.TableEntry(1, 2, 2);
        RoutingTable.TableEntry tb2 = new RoutingTable.TableEntry(3, 3, 0);
        RoutingTable.TableEntry tb3 = new RoutingTable.TableEntry(4, 4, 1);
        List<RoutingTable.TableEntry> list = new ArrayList<>();
        list.add(tb);
        list.add(tb2);
        list.add(tb3);
        testMap.put(3, list);

        RoutingTable.TableEntry tb4 = new RoutingTable.TableEntry(2, 2, 0);
        RoutingTable.TableEntry tb5 = new RoutingTable.TableEntry(1, 1, 1);
        List<RoutingTable.TableEntry> list2 = new ArrayList<>();
        list2.add(tb4);
        list2.add(tb5);
        testMap.put(2, list2);

        String parsed = this.routingTable.convertToStringMessage(testMap);
        assertTrue(parsed == "<2>2,2,0/1,1,1/<3>1,2,2/3,3,0/4,4,1/");
        assertTrue(this.routingTable.convertToStringMessage((this.routingTable.parseTable(parsed))) == parsed);
    }

}