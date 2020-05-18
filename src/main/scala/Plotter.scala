import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.BorderPane
import scalafx.stage.FileChooser

class Plotter extends PrimaryStage{

  title = "Pascal's Loop"
  scene = new Scene(800, 600) {
    val menuBar = new MenuBar
    val saveMenu = new Menu("Save")
    val save = new MenuItem("Save")
    save.onAction = (_: ActionEvent) => {
      val extFilter = new FileChooser.ExtensionFilter("Image",  Seq("*.png, *.jpg"))
      val fc = new FileChooser {
        title = "Save your plot:"
        extensionFilters :+ extFilter
      }
    }
    val saveAs = new MenuItem("Save as")
    val exit = new MenuItem("Exit")
    exit.onAction = (_: ActionEvent) => {

      sys.exit(0)
    }
    val btn: Button = new Button("Click me!"){
      onMouseDragged = (_: MouseEvent) => {
        btn.setText("I see your cursor...")
      }
      onMouseClicked = (_: MouseEvent) => {
        btn.setText("Aha! Clicked!")
      }
    }
    saveMenu.items = List(save, saveAs, new SeparatorMenuItem, exit)
    menuBar.menus = List(saveMenu)


    val rootPane = new BorderPane
    rootPane.top = menuBar
    rootPane.center = btn
    root = rootPane
  }

}
