import java.io.File

import javax.imageio.ImageIO
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.collections.ObservableBuffer
import scalafx.embed.swing.SwingFXUtils
import scalafx.scene.Scene
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, HBox}
import scalafx.stage.FileChooser


object BasicLineChart extends JFXApp {


  stage = new JFXApp.PrimaryStage {
    title = "Line Chart Example"
    scene = new Scene(800, 600) {
      val menu = {
        val menuBar = new MenuBar
        val saveMenu = new Menu("Save")
        val save = new MenuItem("Save as")
        save.onAction = _ => {
          val extFilter = new FileChooser.ExtensionFilter("Image (*.png)",  "*.png")
          val fc = new FileChooser {
            extensionFilters.+=(extFilter)
            title = "Save your plot:"
          }
          val file = fc.showSaveDialog(stage)
          savePng(file)
        }
        val exit = new MenuItem("Exit")
        exit.onAction = _ => {
          sys.exit(0)
        }
        saveMenu.items = List(save, new SeparatorMenuItem, exit)
        menuBar.menus = List(saveMenu)
        menuBar
      }

      val txtBox = new HBox(20) {
        val enterRange = new TextField{
          text = "0; pi"
        }
        val enterRangeText = new Label("Enter range:")
        val enterA = new TextField{
          text = "1"
        }
        val enterAText = new Label("Enter a:")
        val enterB = new TextField{
          text = "1"
        }
        val enterBText = new Label("Enter b:")
        val calculate = new Button("CALCULATE!!"){
          onAction = _ => {
            rootPane.center = areaChart( )
          }
          layoutX = 700
          layoutY = 550
        }
        children = List(enterRangeText, enterRange, enterAText, enterA, enterBText, enterB, calculate)
      }

      def areaChart(): LineChart[Number, Number] = {
        //parse range
        def range(text: => String):Array[BigDecimal] = text.split(";").map{
          case expr if expr contains "*" =>
            val split = expr.split("\\*").map{
              case pi if pi contains "pi" => math.Pi
              case num => num.toDouble
            }
            BigDecimal(split(0) * split(1))
          case pi if pi contains "pi" => BigDecimal(math.Pi)
          case num => BigDecimal(num.toDouble)
        }
        // Helper function to convert a tuple to `XYChart.Data`
        val toChartData = (xy: (Double, Double)) => XYChart.Data[Number, Number](xy._1, xy._2)
        def computeLineChart(a: => Double, b: => Double): LineChart[Number, Number] = {
          val series = new XYChart.Series[Number, Number] {
            name = "Limacon"
            val limacon = for (i <- range(txtBox.enterRange.getText)(0)
              .to(range(txtBox.enterRange.getText)(1), 0.05))
              yield (a / 2 + b * math.cos(i.toDouble) + (a * math.cos(2 * i.toDouble) / 2),
                b * math.sin(i.toDouble) + (a * math.sin(2 * i.toDouble) / 2))
            data = limacon.map(toChartData)
          }
          val lineChart = new LineChart[Number, Number](NumberAxis("Values for X-Axis", -3*(b + a),
            5 + b + a, 0.1), NumberAxis("Values for Y-Axis", -1.5 - b - a, 1.5 + b + a, 1),
            ObservableBuffer(series))
          lineChart.setAnimated(false)
          lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.None)
          lineChart
        }
        computeLineChart(txtBox.enterA.getText.toDouble, txtBox.enterB.getText.toDouble)
      }

      def savePng(file: File): Unit = {
        val img = rootPane.snapshot(null, null)
        try{
          ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file)
        } catch {
          case _: Throwable => ()
        }
      }
      val rootPane = new BorderPane
      rootPane.top = menu
      rootPane.bottom = txtBox
      root = rootPane
    }
  }
}