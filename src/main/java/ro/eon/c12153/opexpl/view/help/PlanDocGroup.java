package ro.any.c12153.opexpl.view.help;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ro.any.c12153.opexpl.entities.PlanDoc;
import ro.any.c12153.opexpl.entities.PlanVal;

/**
 *
 * @author C12153
 */
public class PlanDocGroup {
    
    private static List<PlanVal> sumByDataSetPer(Stream<PlanVal> vals, boolean byAn){
        return vals
                .collect(Collectors.groupingBy(x -> {
                        PlanVal y = new PlanVal();
                        y.setData_set(x.getData_set());
                        y.setAn(x.getAn());
                        if (!byAn) y.setPer(x.getPer());
                        return y;
                    },
                        Collectors.summingDouble(PlanVal::getValoare)
                    )
                )
                .entrySet().stream()
                .map(x -> {x.getKey().setValoare(x.getValue()); return x.getKey();})
                .collect(Collectors.toList());
        
    }
    
    public static final PlanDoc getTotalGroup(List<PlanDoc> docs, boolean byAn){
        PlanDoc rezultat = new PlanDoc();
        rezultat.setCost_driver("TOTAL");
        rezultat.setCost_driver_nume("Total");
        rezultat.setValori(sumByDataSetPer(docs.stream().flatMap(x -> x.getValori().stream()), byAn));
        return rezultat;
    }
    
    public static final List<PlanDoc> getCostDriverTotal(List<PlanDoc> docs, boolean byAn){
        return docs.stream()
                .collect(Collectors.groupingBy(x -> { PlanDoc y = new PlanDoc();
                                                        y.setCoarea(x.getCoarea()); y.setHier(x.getHier()); y.setData_set(x.getData_set()); y.setCost_center(x.getCost_center());
                                                        y.setCost_driver(x.getCost_driver()); y.setCost_driver_nume(x.getCost_driver_nume());
                                                        return y; },
                        Collectors.mapping(PlanDoc::getValori, Collector.of(ArrayList<PlanVal>::new, List<PlanVal>::addAll, (x, y) -> { x.addAll(y); return x; }))))
                .entrySet().stream()
                .map(x -> { x.getKey().setValori(sumByDataSetPer(x.getValue().stream(), byAn)); return x.getKey(); })
                .collect(Collectors.toList());
    }
    
    public static final List<PlanDoc> getOpexCatTotal(List<PlanDoc> docs, boolean byAn){
        return docs.stream()
                .collect(Collectors.groupingBy(x -> { PlanDoc y = new PlanDoc();
                                                        y.setCoarea(x.getCoarea()); y.setHier(x.getHier()); y.setData_set(x.getData_set()); y.setCost_center(x.getCost_center());
                                                        y.setCost_driver(x.getCost_driver()); y.setCost_driver_nume(x.getCost_driver_nume());
                                                        y.setOpex_categ(x.getOpex_categ()); y.setOpex_categ_nume(x.getOpex_categ_nume()); y.setOpex_categ_blocat(x.getOpex_categ_blocat());
                                                        y.setIc_part(x.getIc_part()); y.setIc_part_nume(x.getIc_part_nume()); y.setIc_part_blocat(x.getIc_part_blocat());
                                                        return y; },
                        Collectors.mapping(PlanDoc::getValori, Collector.of(ArrayList<PlanVal>::new, List<PlanVal>::addAll, (x, y) -> { x.addAll(y); return x; }))))
                .entrySet().stream()
                .map(x -> { x.getKey().setValori(sumByDataSetPer(x.getValue().stream(), byAn)); return x.getKey(); })
                .sorted(Comparator.comparing(PlanDoc::getCost_driver).thenComparing(PlanDoc::getOpex_categ).thenComparing(x -> (x.getIc_part() == null ? "" : x.getIc_part())))
                .collect(Collectors.toList());
    }
    
    private static List<PlanVal> sumByTipAndBussLine(Stream<PlanVal> vals){        
        return vals
                .collect(Collectors.groupingBy(x -> {
                        PlanVal y = new PlanVal();
                        y.setData_set(x.getData_set());
                        y.setCont(x.getCont());
                        y.setAn(x.getAn());
                        y.setPer(x.getPer());
                        return y;
                    },
                        Collectors.summingDouble(PlanVal::getValoare)
                    )
                )
                .entrySet().stream()
                .map(x -> {x.getKey().setValoare(x.getValue()); return x.getKey();})
                .collect(Collectors.toList());
    }
    
    public static final PlanDoc getTotalAloc(List<PlanDoc> docs){
        PlanDoc rezultat = new PlanDoc();
        rezultat.setCost_driver("TOTAL");
        rezultat.setCost_driver_nume("Total");
        rezultat.setValori(sumByTipAndBussLine(docs.stream().flatMap(x -> x.getValori().stream())));
        return rezultat;
    }
    
    public static final List<PlanDoc> getCostDriverAloc(List<PlanDoc> docs){
        return docs.stream()
                .collect(Collectors.groupingBy(x -> { PlanDoc y = new PlanDoc();
                                                        y.setCoarea(x.getCoarea()); y.setHier(x.getHier()); y.setData_set(x.getData_set()); y.setCost_center(x.getCost_center());
                                                        y.setCost_driver(x.getCost_driver()); y.setCost_driver_nume(x.getCost_driver_nume());
                                                        return y; },
                        Collectors.mapping(PlanDoc::getValori, Collector.of(ArrayList<PlanVal>::new, List<PlanVal>::addAll, (x, y) -> { x.addAll(y); return x; }))))
                .entrySet().stream()
                .map(x -> { x.getKey().setValori(sumByTipAndBussLine(x.getValue().stream())); return x.getKey(); })
                .collect(Collectors.toList());
    }
}
