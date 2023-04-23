package in.vanna;

import java.util.Random;

import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.AreaPlot;
import tech.tablesaw.plotly.api.BoxPlot;
import tech.tablesaw.plotly.api.BubblePlot;
import tech.tablesaw.plotly.api.Heatmap;
import tech.tablesaw.plotly.api.Histogram;
import tech.tablesaw.plotly.api.Histogram2D;
import tech.tablesaw.plotly.api.HorizontalBarPlot;
import tech.tablesaw.plotly.api.PiePlot;
import tech.tablesaw.plotly.api.VerticalBarPlot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.components.Symbol;
import tech.tablesaw.plotly.traces.BarTrace;
import tech.tablesaw.plotly.traces.BoxTrace;
import tech.tablesaw.plotly.traces.Histogram2DTrace;
import tech.tablesaw.plotly.traces.HistogramTrace;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;
import static tech.tablesaw.aggregate.AggregateFunctions.*;


public class tablesawmain {
	public static void main(String[] args) {
		Table table=Table.read().csv("../in.vanna/src/main/java/in/vanna/titanic.csv");
		System.out.println(table.print());
		
	    Plot.show(
	            AreaPlot.create(
	                "Titanic Robberies by month: Jan 1966-Oct 1975", table, "PassengerId", "Fare"));
	    
	    table = Table.read().csv("../in.vanna/src/main/java/in/vanna/tornadoes_1950-2014.csv");
	    NumericColumn<?> logNInjuries = table.numberColumn("injuries").add(1).logN();
	    logNInjuries.setName("log injuries");
	    table.addColumns(logNInjuries);
	    IntColumn scale = table.intColumn("scale");
	    scale.set(scale.isLessThan(0), IntColumnType.missingValueIndicator());

	    Table summaryTable = table.summarize("fatalities", "log injuries",sum ).by("Scale");

	    Plot.show(
	        HorizontalBarPlot.create(
	            "Tornado Impact",
	            summaryTable,
	            "scale",
	            Layout.BarMode.STACK,
	            "Sum [Fatalities]",
	            "Sum [log injuries]"));

	    Plot.show(
	        VerticalBarPlot.create(
	            "Tornado Impact",
	            summaryTable,
	            "scale",
	            Layout.BarMode.GROUP,
	            "Sum [Fatalities]",
	            "Sum [log injuries]"));

	    Layout layout =
	        Layout.builder()
	            .title("Tornado Impact")
	            .barMode(Layout.BarMode.GROUP)
	            .showLegend(true)
	            .build();

	    String[] numberColNames = {"Sum [Fatalities]", "Sum [log injuries]"};
	    String[] colors = {"#85144b", "#FF4136"};

	    Trace[] traces = new Trace[2];
	    for (int i = 0; i < 2; i++) {
	      String name = numberColNames[i];
	      BarTrace trace =
	          BarTrace.builder(summaryTable.categoricalColumn("scale"), summaryTable.numberColumn(name))
	              .orientation(BarTrace.Orientation.VERTICAL)
	              .marker(Marker.builder().color(colors[i]).build())
	              .showLegend(true)
	              .name(name)
	              .build();
	      traces[i] = trace;
	    }
	    Plot.show(new Figure(layout, traces));
		
	    
	    Table tornadoes = Table.read().csv("../in.vanna/src/main/java/in/vanna/tornadoes_1950-2014.csv");

	    // Get the scale column and replace any values of -9 with the column's missing value indicator
	    IntColumn scale1 = tornadoes.intColumn("scale");
	    scale1.set(scale1.isEqualTo(-9), IntColumnType.missingValueIndicator());

	    // ***************** Plotting **********************

	    // BAR PLOTS

	    // Sum the number of fatalities from each tornado, grouping by scale
	    Table fatalities1 = tornadoes.summarize("fatalities", sum).by("scale");

	    // Plot
	    Plot.show(
	        HorizontalBarPlot.create(
	            "fatalities by scale", // plot title
	            fatalities1, // table
	            "scale", // grouping column name
	            "sum [fatalities]")); // numeric column name

	    // Plot the mean injuries rather than a sum.
	    Table injuries1 = tornadoes.summarize("injuries", mean).by("scale");

	    Plot.show(
	        HorizontalBarPlot.create(
	            "Average number of tornado injuries by scale", injuries1, "scale", "mean [injuries]"));

	    // PIE PLOT
	    Plot.show(PiePlot.create("fatalities by scale", fatalities1, "scale", "sum [fatalities]"));

	    // PARETO PLOT
	    Table t2 = tornadoes.summarize("fatalities", sum).by("State");

	    t2 = t2.sortDescendingOn(t2.column(1).name());
	    Layout layout1 = Layout.builder().title("Tornado Fatalities by State").build();
	    BarTrace trace = BarTrace.builder(t2.categoricalColumn(0), t2.numberColumn(1)).build();
	    Plot.show(new Figure(layout1, trace));
	    
	    Layout layout11 = Layout.builder().title("Tornado Injuries by Scale").build();

	    BoxTrace trace1 =
	        BoxTrace.builder(table.categoricalColumn("scale"), table.nCol("injuries")).build();
	    Plot.show(new Figure(layout11, trace1));
	    
	    Table wines = Table.read().csv("../in.vanna/src/main/java/in/vanna/test_wines.csv");

	    Table champagne =
	        wines.where(
	            wines
	                .stringColumn("wine type")
	                .isEqualTo("Champagne & Sparkling")
	                .and(wines.stringColumn("region").isEqualTo("California")));

	    Figure figure =
	        BubblePlot.create(
	            "Average retail price for champagnes by year and rating",
	            champagne, // table name
	            "highest pro score", // x variable column name
	            "year", // y variable column name
	            "Mean Retail" // bubble size
	            );

	    Plot.show(figure);
	    
	    Table property = Table.read().csv("../in.vanna/src/main/java/in/vanna/sacramento_real_estate_transactions.csv");

	    IntColumn sqft = property.intColumn("sq__ft");
	    IntColumn price = property.intColumn("price");

	    sqft.set(sqft.isEqualTo(0), IntColumnType.missingValueIndicator());
	    price.set(price.isEqualTo(0), IntColumnType.missingValueIndicator());

	    Plot.show(Histogram.create("Distribution of prices", property.numberColumn("price")));

	    Layout layout111 = Layout.builder().title("Distribution of property sizes").build();
	    HistogramTrace trace11 =
	        HistogramTrace.builder(property.numberColumn("sq__ft"))
	            .marker(Marker.builder().color("#B10DC9").opacity(.70).build())
	            .build();
	    Plot.show(new Figure(layout111, trace11));

	    Plot.show(Histogram2D.create("Distribution of price and size", property, "price", "sq__ft"));

	    Plot.show(BoxPlot.create("Prices by property type", property, "type", "price"));
	    
	    
	    
	    Table bush = Table.read().csv("../in.vanna/src/main/java/in/vanna/bush.csv");

	    NumericColumn<?> x = bush.nCol("approval");
	    CategoricalColumn<?> y = bush.stringColumn("who");

	    Layout layout1111 = Layout.builder().title("Approval ratings by agency").build();

	    ScatterTrace trace111 = ScatterTrace.builder(x, y).mode(ScatterTrace.Mode.MARKERS).build();
	    Plot.show(new Figure(layout1111, trace111));

	    // A more complex example involving two traces
	    IntColumn year = bush.dateColumn("date").year();
	    year.setName("year");
	    bush.addColumns(year);
	    bush.dropWhere(bush.intColumn("year").isIn(2001, 2002));
	    Table summary = bush.summarize("approval", mean).by("who", "year");

	    Layout layout2 =
	        Layout.builder()
	            .title("Mean approval ratings by agency and year for 2001 and 2002")
	            .build();

	    Table year1 = summary.where(summary.intColumn("year").isEqualTo(2001));
	    Table year2 = summary.where(summary.intColumn("year").isEqualTo(2002));
	    ScatterTrace trace2 =
	        ScatterTrace.builder(year1.nCol("Mean [approval]"), year1.stringColumn("who"))
	            .name("2001")
	            .mode(ScatterTrace.Mode.MARKERS)
	            .marker(Marker.builder().symbol(Symbol.DIAMOND).color("red").size(10).build())
	            .build();

	    ScatterTrace trace3 =
	        ScatterTrace.builder(year2.nCol("Mean [approval]"), year2.stringColumn("who"))
	            .name("2002")
	            .mode(ScatterTrace.Mode.MARKERS)
	            .marker(Marker.builder().symbol(Symbol.STAR).size(10).color("blue").build())
	            .build();

	    Plot.show(new Figure(layout2, trace2, trace3));
	    
	    
	    Table bush1 = Table.read().csv("../in.vanna/src/main/java/in/vanna/bush.csv");
	    StringColumn yearsMonth = bush1.dateColumn("date").yearMonth();
	    String name = "Year and month";
	    yearsMonth.setName(name);
	    bush1.addColumns(yearsMonth);

	    Figure heatmap = Heatmap.create("Polls conducted by year and month", bush1, name, "who");
	    Plot.show(heatmap);
	    
        Integer size = 500; // Size of the array
        double[] x1 = new double[size]; // Array to hold the random numbers
        double[] y1 = new double[size]; // Array to hold the random numbers

        // Create a Random object
        Random random = new Random();

        // Fill the array with random numbers
        for (int i = 0; i < size; i++) {
            // Generate a random number between -20 and 20 (inclusive)
            x1[i] = random.nextInt(41) - 20;
            y1[i] = random.nextInt(41) - 20;
        }



	      Histogram2DTrace trace4 = Histogram2DTrace.builder(x1, y1).build();
	      Plot.show(new Figure(trace4));
	}
}
