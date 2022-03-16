package fr.atos.springbatchcsvtomysql.config;

import fr.atos.springbatchcsvtomysql.entities.Product;
import fr.atos.springbatchcsvtomysql.listener.MyJobListener;
import fr.atos.springbatchcsvtomysql.processor.ProductProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.core.io.UrlResource;

import javax.sql.DataSource;
import java.util.Date;


@Configuration
@EnableBatchProcessing
public class BatchConfig {

    //a. Reader class object
    @Bean
    public FlatFileItemReader<Product> reader(){
        FlatFileItemReader<Product> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("products.csv")); //src/main/resources
        //reader.setResource(new FileSystemResource("C:/files/products.csv")); //System Drive
        //reader.setResource(new UrlResource("http://abcd.com/files/products.csv")); //URL
        reader.setLineMapper(new DefaultLineMapper() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setDelimiter(DELIMITER_COMMA);
                setNames("prodId", "prodCode", "prodCost");
            }});

            setFieldSetMapper(new BeanWrapperFieldSetMapper() {{
                setTargetType(Product.class);
            }});
        }});
        return reader;
    }
    //b. processor class object
    @Bean
    public ItemProcessor<Product, Product> processor(){
        return new ProductProcessor();
        //avec expression lambda sans faire de class ProductProcessor
        /*return  product ->{
            double cost = product.getProdCost();
            product.setProdDisc(cost * 12/100.0);
            //GST Tax Slab Rates
            product.setProdGst(cost * 22/100.0);
            return product;
        };*/
    }
    @Autowired
    private DataSource dataSource;

    //c. writer class object
    @Bean
    public JdbcBatchItemWriter<Product> writer(){
        JdbcBatchItemWriter<Product> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("INSERT INTO PRODUCTS(PID, PCODE, PCOST, PDISC, PGST) VALUES (:prodId , :prodCode , :prodCost , :prodDisc, :prodGst)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        return writer;
    }
    //d. listener class object
    @Bean
    public JobExecutionListener listener(){
        return new MyJobListener();
        /*//Anonymous class
        return new JobExecutionListener() {

            @Override
            public void beforeJob(JobExecution je) {
                System.out.println("Started Date and Time : " + new Date());
                System.out.println("Status at Starting : " + je.getStatus());
            }

            @Override
            public void afterJob(JobExecution je) {
                System.out.println("End Date and Time : " + new Date());
                System.out.println("Status at Ending : " + je.getStatus());
            }
        };*/
    }

    //e. Autowire Step builder factory
    @Autowired
    private StepBuilderFactory sf;

    //f. Step object
    @Bean
    public Step stepA(){
        return sf.get("stepA")//step name
                 .<Product, Product>chunk(3)//<I,O>chunck
                 .reader(reader()) // reader object
                 .processor(processor()) //processor object
                 .writer(writer()) //writer object
                 .build();
    }
    //g. Autowire Job builder factory
    @Autowired
    private JobBuilderFactory jf;

    //h. Job object
    @Bean
    public Job jobA(){
        return jf.get("jobA")
                .incrementer(new RunIdIncrementer())
                .listener(listener())
                .start(stepA())
                //.next(stepB())
                //.next(stepC())
                .build();
    }
}
