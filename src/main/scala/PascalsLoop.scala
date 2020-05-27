import java.io.File

import javax.imageio.ImageIO
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.collections.ObservableBuffer
import scalafx.embed.swing.SwingFXUtils
import scalafx.scene.Scene
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, HBox}
import scalafx.stage.FileChooser


object BasicLineChart extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    title = "Pascal's loop"
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
      val txtBox = new HBox() {
        val enterRange = new TextField{
          text = "0; pi"
        }
        val enterRangeText = new Label("Range:")
        val enterA = new TextField{
          text = "1"
        }
        val enterAText = new Label("A:")
        val enterB = new TextField{
          text = "1"
        }
        val enterBText = new Label("B:")
        val stepText = new Label("Step:")
        val step = new TextField{
          text = "0.1"
        }
        val plot = new Button("PLOT!"){
          onAction = _ => {
            rootPane.center = lineChart()
          }
          //red button
          style = "-fx-background-color: #ff0000;"
        }
        children = List(enterRangeText, enterRange, enterAText, enterA, enterBText, enterB, stepText, step, plot)
      }

      def lineChart(): LineChart[Number, Number] = {
        val defaultErrorMessage = new Alert(AlertType.Error){
          contentText = "Parsing failed. Wrong input. Building plot with value 1 in this field."
        }
        //parse range
        def range(text: => String):Array[BigDecimal] = text.split(";").map {
          case expr if expr contains "*" =>
            val split = expr.split("\\*").map {
              case pi if pi contains "pi" => math.Pi
              case num => try{
                num.toDouble
              } catch {
                case _: Throwable =>
                  defaultErrorMessage.showAndWait()
                  1
              }
            }
              BigDecimal(split(0) * split(1))
          case pi if pi contains "pi" => BigDecimal(math.Pi)
          case num => BigDecimal(try{
            num.toDouble
          } catch {
            case _: Throwable =>
              defaultErrorMessage.showAndWait()
              1
          })
        }
        // Helper function to convert a tuple to `XYChart.Data`
        val toChartData = (xy: (Double, Double)) => XYChart.Data[Number, Number](xy._1, xy._2)
        def computeLineChart(a: => Double, b: => Double, step: => Double): LineChart[Number, Number] = {
          val series = new XYChart.Series[Number, Number] {
            name = "Pascal's loop"
            val r = range(txtBox.enterRange.getText)
            val limacon = for (i <- r(0).to(r(1), step))
              yield (a / 2 + b * math.cos(i.toDouble) + (a * math.cos(2 * i.toDouble) / 2),
                b * math.sin(i.toDouble) + (a * math.sin(2 * i.toDouble) / 2))
            data = limacon.map(toChartData)
          }
          val xAxis = NumberAxis("X-Axis")
          xAxis autoRanging = true
          val yAxis = NumberAxis("Y-Axis")
          yAxis autoRanging = true
          val lineChart = new LineChart[Number, Number](xAxis, yAxis, ObservableBuffer(series))
          lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.None)
          lineChart.setAnimated(false)
          lineChart
        }
        def parseFromTextBox(tf: =>TextField): Double = {
          try{
            tf.getText.toDouble
          } catch {
            case _: Throwable =>
              defaultErrorMessage.show()
              1
          }
        }
        computeLineChart(parseFromTextBox(txtBox.enterA),
          parseFromTextBox(txtBox.enterB),
          parseFromTextBox(txtBox.step))
      }

      //util function to save snapshot
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