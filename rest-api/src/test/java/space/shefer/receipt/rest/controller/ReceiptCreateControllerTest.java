package space.shefer.receipt.rest.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import space.shefer.receipt.rest.dto.ReceiptCreateDto;
import space.shefer.receipt.rest.service.ItemService;
import space.shefer.receipt.rest.service.ReceiptService;
import space.shefer.receipt.rest.service.report.ReportMetaFilter;
import space.shefer.receipt.rest.util.DateUtil;
import space.shefer.receipt.tests.util.ResourceUtil;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReceiptCreateControllerTest {

  private MockMvc mockMvc;
  private ReceiptCreateController controller;
  private ReceiptService service;

  @Before
  public void setUp() {
    service = mock(ReceiptService.class);
    controller = spy(new ReceiptCreateController(service));
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  public void create() throws Exception {
    String body = ResourceUtil.getResourceAsString("/controller/controllerRequest_create.json", getClass());
    mockMvc.perform(post("/create").content(body)
      .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk());
    ArgumentCaptor<ReceiptCreateDto> filterCaptor= ArgumentCaptor.forClass(ReceiptCreateDto.class);
    verify(service, times(1)).create(filterCaptor.capture());
    ReceiptCreateDto metaFilter =  filterCaptor.getValue();
    assertEquals(DateUtil.parseReceiptDate("20190801T1032"), metaFilter.getDate());
    assertEquals(123629, metaFilter.getSum(), 1e-5);
    assertEquals("936933", metaFilter.getFn());
    assertEquals("832555", metaFilter.getFd());
    assertEquals("535594", metaFilter.getFp());
  }

}
