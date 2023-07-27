package ro.any.c12153.opexpl.view.help;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ro.any.c12153.opexpl.entities.CostCenter;
import ro.any.c12153.opexpl.entities.KeyVal;
import ro.any.c12153.opexpl.entities.KeyValGroup;

/**
 *
 * @author C12153
 */
public class KeyG01ValReduce {
    
    public static List<KeyVal> groupByAn(List<KeyVal> valori, boolean isLeaf){
        if (valori == null) return new ArrayList<>();
        
        Map<Integer, SimpleEntry<KeyVal, Double>> sume = new HashMap<>(); 
        for (KeyVal valoare : valori.stream()
                                .filter(x -> x.getValoare() != null && !x.getValoare().equals(Double.valueOf(0)))
                                .collect(Collectors.toList())){
            KeyVal temp = new KeyVal();
            temp.setAn(valoare.getAn());
            int cheie = temp.hashCode();
        
            if (isLeaf){
                temp.setMod_de(valoare.getMod_de());
                temp.setMod_timp(valoare.getMod_timp());
            }
            
            if (sume.containsKey(cheie)){        
                sume.replace(cheie, new SimpleEntry(
                        isLeaf && temp.getMod_timp().after(sume.get(cheie).getKey().getMod_timp()) ? temp : sume.get(cheie).getKey(),
                        valoare.getValoare() + sume.get(cheie).getValue()
                ));
            } else {
                sume.put(cheie, new SimpleEntry(temp, valoare.getValoare()));
            }
        }
        
        return sume.values().stream()
                .map(x -> {
                    KeyVal y = x.getKey();
                    y.setValoare(x.getValue());
                    return y;
                })
                .sorted(Comparator.comparing(KeyVal::getAn))
                .collect(Collectors.toList());
    }
    
    public static List<KeyValGroup> groupByCostCenter(List<KeyValGroup> pozitii) throws Exception{
        if (pozitii == null) return new ArrayList<>();
        
        Map<Integer, SimpleEntry<KeyValGroup, List<KeyVal>>> elemente = new HashMap<>();
        for (KeyValGroup pozitie: pozitii.stream()
                                    .filter(x -> Boolean.TRUE.equals(x.getLeaf()))
                                    .collect(Collectors.toList())){
            KeyValGroup temp = new KeyValGroup(((CostCenter) pozitie).getJson());
            Integer cheie = temp.hashCode();
            
            if (elemente.containsKey(cheie)){
                if (pozitie.getValori() != null)
                    elemente.get(cheie).getValue().addAll(pozitie.getValori());
            } else {
                List<KeyVal> valori = new ArrayList<>();
                if (pozitie.getValori() != null) valori.addAll(pozitie.getValori());
                elemente.put(cheie, new SimpleEntry(temp, valori));
            }
        }
        
        return Stream.concat(pozitii.stream()
                    .filter(x -> !Boolean.TRUE.equals(x.getLeaf())),
                elemente.values().stream()
                    .map(x -> {
                        KeyValGroup y = x.getKey();
                        y.setValori(groupByAn(x.getValue(), true));
                        return y;
                    })
            )
            .collect(Collectors.toList());
    }
    
    public static void setValuesForCostCenterGroup(List<KeyValGroup> pozitii){
        if (pozitii != null && !pozitii.isEmpty()){
            pozitii.sort(Comparator.comparing(KeyValGroup::getNivel).reversed());
            
            for (int i = 0; i < pozitii.size(); i++){
                final KeyValGroup poz = pozitii.get(i);
                if (!Boolean.TRUE.equals(poz.getLeaf())){
                    pozitii.get(i).setValori(groupByAn(pozitii.stream()
                                .filter(x -> poz.getCod().equals(x.getSuperior_cod()) && x.getValori() != null)
                                .flatMap(x -> x.getValori().stream())
                                .collect(Collectors.toList()), false
                    ));
                }
            }            
            pozitii.sort(Comparator.comparing(KeyValGroup::getNivel).thenComparing(KeyValGroup::getCod));
        }
    }
}
