package com.darshan.birt;

/**
 *
 * @author Darshan Patel
 */
import org.eclipse.birt.report.engine.api.*;
import java.util.logging.Level;
 
/**
 * While adding dependency of 4.6, there is some security exception or problem with jar,
 * which is manually modified.
 * Refer : https://stackoverflow.com/questions/38197480/birt-runtime-4-6-0-error-when-running-genreport
 * Birt Runtime 4.2.0 supports both jdk 1.7, 1.8
 * Birt Runtime 4.6 supports only in 1.8
 * Refer : https://www.eclipse.org/forums/index.php/t/446666/
 */
public class BirtSample {
 
    private IReportEngine engine;
 
    public BirtSample() {
        final EngineConfig config = new EngineConfig();
        engine =  new ReportEngine(config);
        engine.changeLogLevel( Level.WARNING );
    }
 
    public void render(String type) {
        try{
            //Open the report design
            String rptDesignPath="/home/darshan/sample.rptdesign";
            final IReportRunnable design = engine.openReportDesign(rptDesignPath);
 
            //Create task to run and render the report,
            final IRunAndRenderTask task = engine.createRunAndRenderTask(design);
            //Set parent classloader for engine
            task.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, BirtSample.class.getClassLoader());
 
            final IRenderOption options = new RenderOption();
            options.setOutputFormat(type);
            options.setOutputFileName("/home/darshan/TestReport." + options.getOutputFormat());
            if(options.getOutputFormat().equalsIgnoreCase("html")){
                final HTMLRenderOption htmlOptions = new HTMLRenderOption( options);
                htmlOptions.setImageDirectory("img");
                htmlOptions.setHtmlPagination(false);
                htmlOptions.setHtmlRtLFlag(false);
                htmlOptions.setEmbeddable(false);
                htmlOptions.setSupportedImageFormats("PNG");
 
                //set this if you want your image source url to be altered
                //If using the setBaseImageURL, make sure to set image handler to HTMLServerImageHandler
                //htmlOptions.setBaseImageURL("http://myhost/prependme?image=");
            }else if( options.getOutputFormat().equalsIgnoreCase("pdf")){
                final PDFRenderOption pdfOptions = new PDFRenderOption( options );
                pdfOptions.setOption(IPDFRenderOption.PAGE_OVERFLOW, IPDFRenderOption.FIT_TO_PAGE_SIZE);
                pdfOptions.setOption(IPDFRenderOption.PAGE_OVERFLOW, IPDFRenderOption.OUTPUT_TO_MULTIPLE_PAGES);
            } else if(options.getOutputFormat().equalsIgnoreCase("xls")) {
                final EXCELRenderOption excelOptions = new EXCELRenderOption( options );
//                excelOptions.setEnableMultipleSheet(true);
            }
 
            task.setRenderOption(options);
            
            //to pass the dynamic parameter
            task.setParameterValue("site_id_parameter", 266);
 
            //run and render report
            task.run();
 
            task.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
 
    public static void main(String[] args) {
        final BirtSample embeder = new BirtSample();
        embeder.render("html");
        embeder.render("pdf");
        embeder.render("xls");
    }
 
}
