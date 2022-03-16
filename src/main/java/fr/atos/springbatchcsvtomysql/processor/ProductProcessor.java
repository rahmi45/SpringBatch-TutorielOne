package fr.atos.springbatchcsvtomysql.processor;

import fr.atos.springbatchcsvtomysql.entities.Product;
import org.springframework.batch.item.ItemProcessor;

public class ProductProcessor implements ItemProcessor<Product, Product> {
    @Override
    public Product process(Product product) throws Exception {
        double cost = product.getProdCost();
        product.setProdDisc(cost * 12/100.0);
        //GST Tax Slab Rates
        product.setProdGst(cost * 22/100.0);
        return product;
    }
}
