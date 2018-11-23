package com.darshan.birt;

/**
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
public class BirtEmbeder {
 
    private static final IReportEngine ENGINE = new ReportEngine(new EngineConfig()) ;
 
    public BirtEmbeder() {
//        final EngineConfig config = new EngineConfig();
//        engine =  new ReportEngine(config);
        ENGINE.changeLogLevel( Level.WARNING );
    }
 
    public String render(String type) {
        try{
            //Open the report design
            String rptDesignPath="/home/darshan/report.rptdesign";
            final IReportRunnable design = ENGINE.openReportDesign(rptDesignPath);
 
            //Create task to run and render the report,
            final IRunAndRenderTask task = ENGINE.createRunAndRenderTask(design);
            //Set parent classloader for engine
            task.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, BirtEmbeder.class.getClassLoader());
 
            final IRenderOption options = new RenderOption();
            options.setOutputFormat(type);
            System.out.println("Going to generate report");
            String fileName = "/home/darshan/TestReport-" + System.currentTimeMillis() + "." + options.getOutputFormat();
            options.setOutputFileName(fileName);
            if(options.getOutputFormat().equalsIgnoreCase("html")){
                final HTMLRenderOption htmlOptions = new HTMLRenderOption( options);

                htmlOptions.setDisplayFilterIcon(true);//still finding
                htmlOptions.setDisplayGroupIcon(true);//still finding
                htmlOptions.setEnableInlineStyle(true);//instead of style block, it is using inline css
                htmlOptions.setHTMLIDNamespace("darshan");//update all attribute id with darshan_
                
                htmlOptions.setHtmlPagination(true);//still finding
                htmlOptions.setHtmlRtLFlag(false);
                htmlOptions.setEmbeddable(true);//this will remove header and above section
                htmlOptions.setSupportedImageFormats("PNG");
                
                htmlOptions.setImageDirectory("/home/discusit/Desktop/server/wildfly-13.0.0.Final/docs/camsFileSystem/images/");

                htmlOptions.setAppBaseURL("http://192.168.1.152:8080/qms-cloud-web/");
                htmlOptions.setBaseURL("http://192.168.1.152:8080/qms-cloud-web/");
                
		HTMLServerImageHandler imageHandler = new HTMLServerImageHandler();
                htmlOptions.setImageHandler(imageHandler);
 
                //set this if you want your image source url to be altered
                //If using the setBaseImageURL, make sure to set image handler to HTMLServerImageHandler
                htmlOptions.setBaseImageURL("http://192.168.1.152:8080/qms-cloud-web/fileservice/images/");
            }else if( options.getOutputFormat().equalsIgnoreCase("pdf")){
                final PDFRenderOption pdfOptions = new PDFRenderOption( options );
                pdfOptions.setSupportedImageFormats("PNG");
                pdfOptions.setOption(IPDFRenderOption.PAGE_OVERFLOW, IPDFRenderOption.FIT_TO_PAGE_SIZE);
                pdfOptions.setOption(IPDFRenderOption.PAGE_OVERFLOW, IPDFRenderOption.OUTPUT_TO_MULTIPLE_PAGES);
            } else if(options.getOutputFormat().equalsIgnoreCase("xls")) {
                final EXCELRenderOption excelOptions = new EXCELRenderOption( options );
//                excelOptions.setEnableMultipleSheet(true);
            }
 
            task.setRenderOption(options);
            
            //to pass the dynamic parameter
//            task.setParameterValue("site_id_parameter", 266);
 
            //run and render report
            task.run();
 
            task.close();
            return fileName;
        } catch(Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
 
    public static void main(String[] args) {
        final BirtEmbeder embeder = new BirtEmbeder();
        embeder.render("html");
        embeder.render("pdf");
        embeder.render("xls");
    }
 
}
