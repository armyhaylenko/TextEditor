import java.io.File

import javax.imageio.ImageIO
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.collections.ObservableBuffer
import scalafx.embed.swing.SwingFXUtils
import scalafx.scene.Scene
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}
import scalafx.scene.control.{Menu, MenuBar, MenuItem, SeparatorMenuItem}
import scalafx.scene.image.WritableImage
import scalafx.scene.layout.BorderPane
import scalafx.stage.FileChooser

object BasicLineChart extends JFXApp {


  stage = new JFXApp.PrimaryStage {
    title = "Line Chart Example"
    scene = new Scene {
      val lineChart = {

        val xAxis = NumberAxis("Values for X-Axis", -3 * math.Pi, 3 * math.Pi, 0.1)
        val yAxis = NumberAxis("Values for Y-Axis", -1.5, 1.5, 1)

        // Helper function to convert a tuple to `XYChart.Data`
        val toChartData = (xy: (Double, Double)) => XYChart.Data[Number, Number](xy._1, xy._2)

        val series1 = new XYChart.Series[Number, Number] {
          name = "Series 1"
          val sin = for (i <- BigDecimal(-2 * math.Pi) to(BigDecimal(2 * math.Pi), 0.1))
            yield (i.toDouble, math.sin(i.toDouble))
          data = sin.map(toChartData)
        }


        val lineChart = new LineChart[Number, Number](xAxis, yAxis, ObservableBuffer(series1))
        lineChart.setAnimated(false)


        lineChart

      }

      def savePng(file: File): Unit = {
        val img = lineChart.snapshot(null, new WritableImage(500, 400))
        ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file)
      }

      val menuBar = new MenuBar
      val saveMenu = new Menu("Save")
      val save = new MenuItem("Save")
      save.onAction = (_) => {
        val extFilter = new FileChooser.ExtensionFilter("Image",  Seq("*.png, *.jpg"))
        val fc = new FileChooser {
          extensionFilters :+ extFilter
          title = "Save your plot:"
        }
        val file = fc.showSaveDialog(stage)
        savePng(file)
      }
      val exit = new MenuItem("Exit")
      exit.onAction = (_) => {
        sys.exit(0)
      }
      saveMenu.items = List(save, new SeparatorMenuItem, exit)
      menuBar.menus = List(saveMenu)

      val rootPane = new BorderPane
      rootPane.top = menuBar
      rootPane.center = lineChart
      root = rootPane
    }
  }
}