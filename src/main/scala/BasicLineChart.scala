import java.io.File

import javax.imageio.ImageIO
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.collections.ObservableBuffer
import scalafx.embed.swing.SwingFXUtils
import scalafx.scene.Scene
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}
import scalafx.scene.control._
import scalafx.scene.image.WritableImage
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
          val extFilter = new FileChooser.ExtensionFilter("Image",  Seq("*.png, *.jpg"))
          val fc = new FileChooser {
            extensionFilters :+ extFilter
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

      val textFieldsPane = new HBox(20) {
        val enterRange = new TextField{
          text = "Enter range:"
        }
        val enterA = new TextField{
          text = "Enter a:"
        }
        val enterB = new TextField{
          text = "Enter b:"
        }
        children = List(enterRange, enterA, enterB)
      }

      lazy val lineChart = {
        val a = textFieldsPane.enterA.getText.toDouble
        val b = textFieldsPane.enterB.getText.toDouble

        val xAxis = NumberAxis("Values for X-Axis", -3*(b + a), 5 + b + a, 0.1)
        val yAxis = NumberAxis("Values for Y-Axis", -1.5 - b - a, 1.5 + b + a, 1)
        //parse range
        val range = textFieldsPane.enterRange.getText.split(";").map{
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
        val series1 = new XYChart.Series[Number, Number] {
          name = "Limacon"
          val loop = for (i <- range(0).to(range(1), 0.1))
            yield (a / 2 + b * math.cos(i.toDouble) + (a * math.cos(2 * i.toDouble) / 2),
              b * math.sin(i.toDouble) + (a * math.sin(2 * i.toDouble) / 2))
          data = loop.map(toChartData)
        }
        val lineChart = new LineChart[Number, Number](xAxis, yAxis, ObservableBuffer(series1))
        lineChart
      }

      def savePng(file: File): Unit = {
        val img = lineChart.snapshot(null, new WritableImage(500, 400))
        ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file)
      }
      val calculate = new Button("CALCULATE!!"){
        onAction = _ => {
          var mutableLineChart = lineChart
          rootPane.center = mutableLineChart
        }
        layoutX = 700
        layoutY = 550
      }
      val rootPane = new BorderPane
      rootPane.top = menu
      rootPane.bottom = textFieldsPane
      rootPane.right = calculate
      root = rootPane
    }
  }
}