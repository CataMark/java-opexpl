package ro.any.c12153.opexpl.view.help;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ro.any.c12153.opexpl.entities.PlanDoc;
import ro.any.c12153.opexpl.entities.PlanVal;

/**
 *
 * @author C12153
 */
public class PlanCDAlocareReduce {
    
    private static List<PlanVal> groupByTipAndBussLine(List<PlanVal> valori){
        if (valori == null) return new ArrayList<>();
        
        Map<Integer, SimpleEntry<PlanVal, Double>> sume = new HashMap<>();
        for (PlanVal valoare : valori.stream()
                                    .filter(x -> x.getValoare() != null && !x.getValoare().equals(Double.valueOf(0)))
                                    .collect(Collectors.toList())){
            PlanVal temp = new PlanVal();
            temp.setData_set(valoare.getData_set());
            temp.setCont(valoare.getCont()); //contine tipul de valoare "planificat" sau "alocat"
            temp.setAn(valoare.getAn());
            temp.setPer(valoare.getPer()); //contine codul de business line
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
    
    public static Optional<PlanDoc> getTotalGroup (List<PlanDoc> pozitii){
        if (pozitii == null || pozitii.isEmpty()) return Optional.empty();
        
        PlanDoc total = new PlanDoc();
        total.setCoarea(pozitii.get(0).getCoarea());
        total.setHier(pozitii.get(0).getHier());
        total.setData_set(pozitii.get(0).getData_set());
        total.setCost_driver(pozitii.get(0).getCost_driver());
        total.setCost_driver_nume(pozitii.get(0).getCost_driver_nume());
        total.setOpex_categ_nume("Total");
        total.setOpex_categ_blocat(false);
        total.setValori(groupByTipAndBussLine(pozitii.stream()
                                        .filter(x -> x.getValori() != null)
                                        .flatMap(x -> x.getValori().stream())
                                        .collect(Collectors.toList())));
        return Optional.of(total);
    }
    
    public static List<PlanDoc> groupByCostCenter(List<PlanDoc> pozitii) throws Exception{
        if (pozitii == null) return new ArrayList<>();
        
        Map<Integer, SimpleEntry<PlanDoc, List<PlanVal>>> elemente = new HashMap<>();
        for (PlanDoc pozitie : pozitii.stream()
                                    .filter(x -> Boolean.TRUE.equals(x.getCost_center_leaf()))
                                    .collect(Collectors.toList())){
            PlanDoc temp = new PlanDoc();
            temp.setHier(pozitie.getHier());
            temp.setData_set(pozitie.getData_set());
            temp.setCost_center(pozitie.getCost_center());
            temp.setCost_center_nume(pozitie.getCost_center_nume());
            temp.setCost_center_super(pozitie.getCost_center_super());
            temp.setCost_center_blocat(pozitie.getCost_center_blocat());
            temp.setCost_center_leaf(pozitie.getCost_center_leaf());
            temp.setCost_center_nivel(pozitie.getCost_center_nivel());
            temp.setCost_driver(pozitie.getCost_driver());
            temp.setOpex_categ(pozitie.getOpex_categ());
            temp.setIc_part(pozitie.getIc_part());
            temp.setIc_part_nume(pozitie.getIc_part_nume());
            temp.setIc_part_blocat(pozitie.getIc_part_blocat());
            int cheie = temp.hashCode();
            
            if (elemente.containsKey(cheie)){
                if (pozitie.getValori() != null)
                    elemente.get(cheie).getValue().addAll(pozitie.getValori());
            } else {
                List<PlanVal> valori = new ArrayList<>();
                if (pozitie.getValori() != null) valori.addAll(pozitie.getValori());
                elemente.put(cheie, new SimpleEntry(temp, valori));
            }
        }
        return Stream.concat(pozitii.stream()
                    .filter(x -> !Boolean.TRUE.equals(x.getCost_center_leaf())),
                elemente.values().stream()
                    .map(x -> {
                        PlanDoc y = x.getKey();
                        y.setValori(groupByTipAndBussLine(x.getValue()));
                        return y;
                    })
            )
            .collect(Collectors.toList());
    }
    
    public static void setValuesForCostCenterGroup(List<PlanDoc> pozitii){
        if (pozitii != null && !pozitii.isEmpty()){
            pozitii.sort(Comparator.comparing(PlanDoc::getCost_center_nivel).reversed());
            
            for (int i = 0; i < pozitii.size(); i++){
                final PlanDoc poz = pozitii.get(i);
                if (!Boolean.TRUE.equals(poz.getCost_center_leaf())){
                    pozitii.get(i).setValori(groupByTipAndBussLine(pozitii.stream()
                            .filter(x -> poz.getCost_center().equals(x.getCost_center_super()) && x.getValori() != null)
                            .flatMap(x -> x.getValori().stream())
                            .collect(Collectors.toList())
                    ));
                } 
            }
            pozitii.sort(Comparator
                    .comparing(PlanDoc::getCost_center_nivel)
                    .thenComparing(PlanDoc::getCost_center)
                    .thenComparing(x -> {return (x.getIc_part() == null ? "_" : x.getIc_part());})
            );
        }
    }
}
