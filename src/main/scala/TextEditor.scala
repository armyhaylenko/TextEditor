import java.awt.Desktop
import java.io.{File, FileWriter, PrintWriter}

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.scene.layout.BorderPane
import scalafx.scene.text.Font
import scalafx.stage.FileChooser

import scala.io.Source
import scala.util.Random

object TextEditor extends JFXApp {
  val powerShell =
    new File("C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe")
  val gitBash = new File("C:\\Program Files\\Git\\git-bash.exe")
  val txtFilter = new FileChooser.ExtensionFilter("Text file", "*.txt")
  val fc = new FileChooser( ) {
    extensionFilters += txtFilter
  }
  val hackerStyle = getClass.getResource("HackerMode.css").toExternalForm
  val defaultStyle = getClass.getResource("Default.css").toExternalForm

  def saveTxtToFile(txt: String, filePath: String = "src/main/resources/Untitled.txt"): Unit = {
    val pw = new PrintWriter(new FileWriter(new File(filePath)))
    pw.write(txt)
    pw.close( )
  }


  stage = new PrimaryStage {
    title = "Sentence Text File Editor"
    scene = new Scene(800, 600) {
      val menuBar = new MenuBar {
        val fileMenu = new Menu("File") {
          val openItem = new MenuItem("Open...") {
            onAction = _ => {
              val txtFile = Source.fromFile(fc.showOpenDialog(stage))
              textArea.text = txtFile.getLines( ).mkString("\n")
            }
          }
          val saveItem = new MenuItem("Save...") {
            onAction = _ => saveTxtToFile(textArea.getText)
          }
          val saveAsItem = new MenuItem("Save as...") {
            onAction = _ => saveTxtToFile(textArea.getText, fc.showSaveDialog(stage).getPath)
          }
          items = List(openItem, saveItem, saveAsItem)
        }
        val editMenu = new Menu("Edit") {
          val clearItem = new MenuItem("Clear") {
            onAction = _ => textArea.text = ""
          }
          val fontSizeItem = new MenuItem("Font size") {
            onAction = _ => {
              val dialog = new TextInputDialog("11") {
                headerText = "Enter desired font size"
              }
              dialog.showAndWait( ) match {
                case Some(value) => changeFontSize(value.toInt)
                case None => ()
              }
            }
          }
          items = List(clearItem, fontSizeItem)
        }
        val toolsMenu = new Menu("Tools") {
          val wordCountItem = new MenuItem("Word count") {
            onAction = _ => {
              val count = textArea.getText.split(" ").length
              new Alert(AlertType.Information) {
                headerText = "Word count"
                contentText = s"Your text contains $count word (s)"
              }.showAndWait( )
            }
          }
          val lineCountItem = new MenuItem("Line count") {
            onAction = _ => {
              val count = textArea.getText.split("\n").length
              new Alert(AlertType.Information) {
                headerText = "Line count"
                contentText = s"Your text contains $count line(s)"
              }.showAndWait( )
            }
          }
          val randomizeItem = new MenuItem("Randomize!") {
            onAction = _ => {
              textArea setText Random.shuffle(textArea.getText.toList).mkString("")
            }
          }
          val hackerModeItem = new CheckMenuItem("Hacker mode") {
            onAction = _ => {
              if (this.selected.get) {
                textArea.stylesheets = List(hackerStyle)
              } else {
                textArea.stylesheets = List(defaultStyle)
              }
            }
          }
          items = List(wordCountItem, lineCountItem, randomizeItem, hackerModeItem)
        }

        val terminalsMenu = new Menu("Terminals"){
          val powerShellItem = new MenuItem("Powershell"){
            onAction = _ => {
              Desktop.getDesktop.open(powerShell)
            }
          }
          val gitBashItem = new MenuItem("Git Bash"){
            onAction = _ => {
              Desktop.getDesktop.open(gitBash)
            }
          }
          items = List(powerShellItem, gitBashItem)
        }
        val helpMenu = new Menu("Help") {
          val helpItem = new MenuItem("Help..."){
            onAction = _ => {
              new Alert(AlertType.Information){
                headerText = "Help"
                contentText = "Randomize - shuffles your text character by character.\n" +
                  "Hacker mode - turns your editor into terminal-looking window.\n" +
                  "Save - saves your txt file into \"resources\" folder."
              }.showAndWait()
            }
          }


          val aboutItem = new MenuItem("About..."){
            onAction = _ => {
              new Alert(AlertType.Information){
                headerText = "About"
                contentText = "Created by Oleksandr Mykhailenko, actual version - June 2020."
              }.showAndWait()
            }
          }
          items = List(helpItem, aboutItem)
        }
        val quitMenu = new Menu("Quit") {
          val quitItem = new MenuItem("Quit") {
            onAction = _ => {
              val quitMessage = new Alert(AlertType.Confirmation) {
                contentText = "Are you sure you want to quit?"
              }
              quitMessage.showAndWait( ) match {
                case Some(ButtonType.OK) => sys.exit(0)
                case _ => ()
              }
            }
          }
          items = List(quitItem)
        }
        style = "-fx-background-color: #008b8b"
        menus = List(fileMenu, editMenu, toolsMenu, terminalsMenu, helpMenu, quitMenu)
      }


      val textArea = new TextArea

      def changeFontSize(size: Int): Unit = {
        textArea.setFont(Font(size))
      }

      val rootPane = new BorderPane
      rootPane.top = menuBar
      rootPane.center = textArea
      root = rootPane
    }
  }
}
