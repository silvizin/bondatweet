package com.bondatweet.myapp;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.apache.lucene.search.Query;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryParser;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.FacetBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.FacetExecutor.Post.Filtered;
import org.elasticsearch.search.facet.FacetBuilders.*;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.elasticsearch.search.query.QueryPhase;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.FilterBuilders.*;
import org.elasticsearch.index.query.QueryBuilders.*;

import static org.elasticsearch.index.query.FilterBuilders.*;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.node.NodeBuilder.*;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		//logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		int k=0;
		
		try {
			Document doc = Jsoup.connect(
					"http://finance.naver.com/item/main.nhn?code=005930").get();// �쇱꽦�꾩옄
																				// 二쇱떇�섏씠吏�																				// 遺덈윭�ㅺ린
			Element t = doc.getAllElements().get(9);// �꾩옱 二쇨��뺣낫留�媛�졇�⑤떎
			String juga = t.attr("content");
		//	System.out.println(juga + " ddddd");
		//	System.out.println(juga.substring(5, 14));
			String hi = juga.substring(5, 14);
			String hi2 = hi.replace(",", "");

			 k = Integer.parseInt(hi2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Node node = nodeBuilder().local(true).clusterName("es_test").data(true).node();
		Client client = node.client();

// on shutdown

		
		/*Node node =
			    nodeBuilder()
			        .settings(ImmutableSettings.settingsBuilder().put("http://localhost:9200", false)).clusterName("es_test")
			        .client(true)
			    .node();

			Client client = node.client();*/

		
		
	//	Node node = nodeBuilder().local(true).node();
	//	Client client = node.client();
		
		String qu = " \"created_at\" : {  \"gt\" : \"now-2d\" }\" ";
		String ce = " \"twitter_river2\" : {     \"terms\" : { \"field\" : \"text\", \"size\" : 10, \"exclude\" : [\"t.co\", \"http\",\"samsung\",\"galaxy\",\"rt\",\"a\",\"in\",\"to\",\"for\",\"the\",\"of\",\"and\",\"https\",\"is\"] }"
				+ "} ";
		/*SearchResponse response = client.prepareSearch("twitter_river2")
		        .setTypes("status")
		        .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
		        .setQuery(QueryBuilders.rangeQuery(qu)).setFacets(facets)             // Query
		        .setPostFilter(FilterBuilders.rangeFilter("age").from(12).to(18))   // Filter
		        .setFrom(0).setSize(60).setExplain(true)
		        .execute()
		        .actionGet();
		*/
	//	FacetBuilders ce = new FacetBuilders();
	//	ce.filterFacet(termsFilter("fiedls", "text").`)
		
		
		
	//	FacetBuilders facet2 = FacetBuilders.termsFacet("facets").field("text").exclude("t.co", "http","samsung","galaxy","rt","a","in","to","for","the","of","and","https","is").size(10);
	 
		
		/*facet2.field("text").
		exclude("t.co", "http","samsung","galaxy","rt","a","in","to","for","the","of","and","https","is").size(10);*/
		
		QueryBuilder qb = matchQuery(
			    "created_at",                  
			    "2015-09-02"   
			);
		
		String qbt="null";
		TermFilterBuilder fb = FilterBuilders.termFilter("created_at", "2015-09-03");
		

		/*   "query" : {
		        "filtered" : {
		            "filter" : {
		                "range" : {
		                    "created_at" : {
		                       "gt" : "now-1d"
		                    }
		                }
		            }
		        }
		    },*/
		String queryString2 = "  \"filtered\" : {   \"filter\" : {   \"range\" : { \"created_at\" : {"
				+ "    \"gt\" : \"now-1d\"   }  }  }        }";
		/*FilterBuilders filter = FilterBuilders.rangeFilter("filter").gt("now-1d").cacheKey("created_at");
		QueryBuilders qb = QueryBuilders.filteredQuery(queryBuilder, filterBuilder)*/
		QueryBuilder kk = queryStringQuery(queryString2).field("created_at");   // (queryString2);
	//	System.out.println(QueryBuilders.rangeQuery("created_at").gt("now-1d"));
		System.out.println(kk.toString());
		SearchResponse response = client.prepareSearch("twitter_river2")
		        .setTypes("twitter")
		        .setSearchType(SearchType.COUNT)
		        .setQuery(kk).addFacet(FacetBuilders.termsFacet("twitter_river2").field("text").exclude("t.co", "http","samsung","galaxy","rt","a","in","to","for","the","of","and","https","is").size(10))            // Query
		        //.setPostFilter(FilterBuilders.rangeFilter("age").from(12).to(18))   // Filter
		        .setFrom(0).setSize(60).setExplain(true)
		        .execute()
		        .actionGet();
	
		String result = response.toString();
		System.out.println(result);
		
		client.close();
		
		/*
		RangeQueryBuilder quf = QueryBuilders.rangeQuery(qu);
		fb.termsFilter("field", "text").
		FacetBuilder fce = FacetBuilder.Mode.POST.;
		
		fce.facetFilter()
		countSearchResponse sr = node.client().prepareSearch()
		        .setQuery(qu)
		        .addFacet(ce)
		        .execute().actionGet();*/
		
		
		//QueryBuilders q1 = filteredqu
		
	/*	SearchResponse sr = node.client().prepareSearch()
		        .setQuery()
		        .addFacet(  add a facet  )
		        .execute().actionGet();
		*/
		
		
		
		
		
		
		model.addAttribute("juga", k);
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
}
