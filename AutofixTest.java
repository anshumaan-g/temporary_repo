package com.firm58.web.servlet;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dozer.Mapper;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.WebApplicationContext;

import com.firm58.rating.service.CrossTabRatingReportService;
import com.firm58.rating.service.RatingReportCsvService;
import com.firm58.rating.service.StandardRatingReportService;
import com.firm58.rating.service.impl.StandardRatingReportCsvService;
import com.firm58.report.model.RatingReportConfig;
import com.firm58.report.model.RatingReportTypeEnum;
import com.firm58.report.service.ReportService;
import com.firm58.share.exception.F58Exception;
import com.firm58.web.gwt.data.server.ClientDataConverter;
import com.firm58.web.gwt.rating.report.client.RatingReportConfigDTO;

@RunWith(MockitoJUnitRunner.class)
public class NewRatingReportCSVExportServletTest {

    private static final String CD_MAPPER = "cdMapper";
    private Mapper antMapper;
    @Mock
    private Mapper mapper;
    @Mock
    private ServletConfig servletConfig;
    @Mock
    private ServletContext servletContext;
    @Mock
    private WebApplicationContext webApplicationContext;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private HttpServletResponse response;
    @Mock
    private CrossTabRatingReportService crossTabRatingReportService;
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();
    @Mock
    private StandardRatingReportService standardRatingReportService;
    @Mock
    private ReportService reportService;
    @Mock
    private RatingReportCsvService ratingReportCsvService;
    @Spy
    private final NewRatingReportCSVExportServlet newRatingReportCSVExportServlet = new NewRatingReportCSVExportServlet();

    @Captor
    public  ArgumentCaptor<String> captor;
    @Rule
    public final ErrorCollector errorCollector = new ErrorCollector();
    @Before
    public void setUp() throws Exception {
        newRatingReportCSVExportServlet.init(servletConfig);
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(anyString())).thenReturn(webApplicationContext);
        when(request.getSession()).thenReturn(session);
        when(webApplicationContext.getBean(anyString())).thenReturn(ratingReportCsvService);
        Field field = ClientDataConverter.class.getDeclaredField(CD_MAPPER);
        field.setAccessible(true);
        antMapper = (Mapper) field.get(newRatingReportCSVExportServlet);
        field.set(newRatingReportCSVExportServlet, mapper);
        standardRatingReportService = mock(StandardRatingReportService.class);
        ratingReportCsvService = new StandardRatingReportCsvService(standardRatingReportService, null, reportService);
    }

    @Test
    public void exportCrossReport() throws Exception {
        // Arrange
        RatingReportConfigDTO ratingReportConfigDTO = new RatingReportConfigDTO();
        when(session.getAttribute(anyString())).thenReturn(ratingReportConfigDTO);
        RatingReportConfig ratingReportConfig = mock(RatingReportConfig.class);
        when(mapper.map(ratingReportConfigDTO, RatingReportConfig.class)).thenReturn(ratingReportConfig);
        when(ratingReportConfig.getReportType()).thenReturn(RatingReportTypeEnum.CROSS);

        // Act
        newRatingReportCSVExportServlet.doGet(request, response);

        // Assert
        verify(webApplicationContext, times(1)).getBean(captor.capture());
        String actualBeanName = captor.getValue();
        verify(webApplicationContext, times(1)).getBean(anyString());
        errorCollector.checkThat(actualBeanName,is("crossTabRatingReportCsvService"));
    }

    @Test
    public void exportStandardReport() throws Exception {
        // Arrange
        when(request.getParameter(any())).thenReturn("TestParam");
        RatingReportConfigDTO ratingReportConfigDTO = new RatingReportConfigDTO();
        when(session.getAttribute(anyString())).thenReturn(ratingReportConfigDTO);
        RatingReportConfig ratingReportConfig = mock(RatingReportConfig.class);
        when(mapper.map(ratingReportConfigDTO, RatingReportConfig.class)).thenReturn(ratingReportConfig);
        when(ratingReportConfig.getReportType()).thenReturn(RatingReportTypeEnum.STANDARD);
       
        // Act
        newRatingReportCSVExportServlet.doGet(request, response);

        // Assert
        verify(webApplicationContext, times(1)).getBean(captor.capture());
        String actualBeanName = captor.getValue();
        verify(webApplicationContext, times(1)).getBean(anyString());
        errorCollector.checkThat(actualBeanName,is("standardRatingReportCsvService"));
        return;
    }

    @Test
    public void givenNoReportTypeThenException() throws Exception {
        // Arrange
        // check
        when(request.getParameter(any())).thenReturn("TestParam");
        RatingReportConfigDTO ratingReportConfigDTO = new RatingReportConfigDTO();
        when(session.getAttribute(anyString())).thenReturn(ratingReportConfigDTO);
        RatingReportConfig ratingReportConfig = mock(RatingReportConfig.class);
        when(mapper.map(ratingReportConfigDTO, RatingReportConfig.class)).thenReturn(ratingReportConfig);
        doThrow(new F58Exception("error")).when(ratingReportConfig).getReportType();
       
        //assert
        expectedException.expect(ServletException.class);

        // Act
        newRatingReportCSVExportServlet.doGet(request, response);
    }

}
