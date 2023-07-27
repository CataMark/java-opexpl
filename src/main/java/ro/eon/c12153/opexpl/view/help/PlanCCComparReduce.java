package ro.any.c12153.opexpl.view.help;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.opexpl.entities.PlanDoc;
import ro.any.c12153.opexpl.entities.PlanVal;

/**
 *
 * @author C12153
 */
public class PlanCCComparReduce {
    
    private static List<PlanVal> groupByAn(List<PlanVal> valori){
        if (valori == null) return new ArrayList<>();
        
        Map<Integer, SimpleEntry<PlanVal, Double>> sume = new HashMap<>();
        for (PlanVal valoare : valori.stream()
                                    .filter(x -> x.getValoare() != null && !x.getValoare().equals(Double.valueOf(0)))
                                    .collect(Collectors.toList())){
            PlanVal temp = new PlanVal();
            temp.setData_set(valoare.getData_set());
            temp.setAn(valoare.getAn());
            int cheie = temp.hashCode();
            
            if (sume.containsKey(cheie)){
                sume.replace(cheie, new SimpleEntry(temp, valoare.getValoare() + sume.get(cheie).getValue()));
            } else {
                sume.put(cheie, new SimpleEntry(temp, valoare.getValoare()));
            }
        }
        return sume.values().stream()
                .map(x -> {
                    PlanVal y = x.getKey();
                    y.setValoare(x.getValue());
                    return y;
                })
                .collect(Collectors.toList());
    }
    
    public static List<PlanDoc> getCostDriverGroups(List<PlanDoc> pozitii){
        if (pozitii == null) return new ArrayList<>();
            
        Map<Integer, SimpleEntry<PlanDoc, List<PlanVal>>> cdrivers = new HashMap<>();
        for (PlanDoc document : pozitii.stream()
                                    .filter(x -> x.getOpex_categ() != null)
                                    .collect(Collectors.toList())){
            PlanDoc temp = new PlanDoc();
            temp.setCoarea(document.getCoarea());
            temp.setHier(document.getHier());
            temp.setData_set(document.getData_set());
            temp.setCost_center(document.getCost_center());
            temp.setCost_center_nume(document.getCost_center_nume());
            temp.setCost_center_blocat(document.getCost_center_blocat());
            temp.setCost_driver(document.getCost_driver());
            temp.setCost_driver_nume(document.getCost_driver_nume());
            temp.setCost_driver_central(document.getCost_driver_central());
            int cheie = temp.hashCode();

            if (cdrivers.containsKey(cheie)){
                if (document.getValori() != null) 
                    cdrivers.get(cheie).getValue().addAll(document.getValori());
            } else {
                List<PlanVal> valori = new ArrayList<>();
                if (document.getValori() != null) valori.addAll(document.getValori());
                cdrivers.put(cheie, new SimpleEntry(temp, valori));
            }
        }
        
        return cdrivers.values().stream()
                .map(x -> {
                    PlanDoc y = x.getKey();
                    y.setValori(groupByAn(x.getValue()));
                    return y;
                })
                .sorted(Comparator.comparing(PlanDoc::getCost_driver_central).thenComparing(PlanDoc::getCost_driver))
                .collect(Collectors.toList());
    }
    
    public static Optional<PlanDoc> getTotalGroup(List<PlanDoc> pozitii){
        if (pozitii == null || pozitii.isEmpty()) return Optional.empty();
        
        PlanDoc total = new PlanDoc();
        total.setCoarea(pozitii.get(0).getCoarea());
        total.setHier(pozitii.get(0).getHier());
        total.setData_set(pozitii.get(0).getData_set());
        total.setCost_center(pozitii.get(0).getCost_center());
        total.setCost_center_nume(pozitii.get(0).getCost_center_nume());
        total.setCost_center_blocat(pozitii.get(0).getCost_center_blocat());
        total.setCost_driver("TOTAL");
        total.setCost_driver_nume("Total");
        total.setValori(groupByAn(pozitii.stream()
                    .filter(x -> x.getValori() != null)
                    .flatMap(x -> x.getValori().stream())
                    .collect(Collectors.toList())));
        return Optional.of(total);
    }
    
    public static List<PlanDoc> groupByOpexCateg(List<PlanDoc> pozitii){
        if (pozitii == null || pozitii.isEmpty()) return new ArrayList<>();
        
        Map<Integer, SimpleEntry<PlanDoc, List<PlanVal>>> sume = new HashMap<>();
        for (PlanDoc document : pozitii.stream()
                                    .filter(x -> x.getOpex_categ() != null)
                                    .collect(Collectors.toList())){
            PlanDoc temp = new PlanDoc();
            temp.setCoarea(document.getCoarea());
            temp.setHier(document.getHier());
            temp.setData_set(document.getData_set());
            temp.setCost_center(document.getCost_center());
            temp.setCost_center_nume(document.getCost_center_nume());
            temp.setCost_center_blocat(document.getCost_center_blocat());
            temp.setCost_driver(document.getCost_driver());
            temp.setCost_driver_nume(document.getCost_driver_nume());
            temp.setCost_driver_central(document.getCost_driver_central());
            temp.setOpex_categ(document.getOpex_categ());
            temp.setOpex_categ_nume(document.getOpex_categ_nume());
            temp.setOpex_categ_blocat(document.getOpex_categ_blocat());
            temp.setIc_part(document.getIc_part());
            temp.setIc_part_nume(document.getIc_part_nume());
            temp.setIc_part_blocat(document.getIc_part_blocat());
            int cheie = temp.hashCode();
            
            if (sume.containsKey(cheie)){
                if (document.getValori() != null)
                    sume.get(cheie).getValue().addAll(document.getValori());
            } else {
                List<PlanVal> valori = new ArrayList<>();
                if (document.getValori() != null) valori.addAll(document.getValori());
                sume.put(cheie, new SimpleEntry(temp, valori));
            }
        }
        
        return sume.values().stream()
                .map(x -> {
                    PlanDoc y = x.getKey();
                    y.setValori(groupByAn(x.getValue()));
                    return y;})
                .sorted(
                        Comparator.comparing(PlanDoc::getCost_driver)
                            .thenComparing(PlanDoc::getOpex_categ)
                            .thenComparing(x -> (x.getIc_part() == null ? "" : x.getIc_part()))
                )
                .collect(Collectors.toList());
    }
}
