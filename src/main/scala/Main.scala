import scala.collection.mutable
import scala.io.Source


object Main extends App {

  val filename = "TrainingSets\\3.txt"
  val lines = Source.fromFile(filename).getLines.toList
  val height = lines(0).trim().toInt
  val width = lines(1).trim().toInt
  val board = lines.drop(2).flatten.filterNot((x: Char) => x.isWhitespace)

  val allNodes = for {node <- board.zipWithIndex } yield
    if (node._1 == 'x') WorldNode(node._2%width, node._2/width.floor.toInt, true)
    else WorldNode(node._2%width, node._2/width.floor.toInt, false)

  val world = new World(width, height, board)

  world.start.distanceToGoal = Math.abs(world.goal.x - world.start.x) + Math.abs(world.goal.y - world.start.y)
  world.start.update(0, world.start, world.goal, null)

  val path = doEverything(new mutable.PriorityQueue[WorldNode]() += world.start, Nil, 0)

  println(world.toString(path))


  def doEverything(openList: mutable.PriorityQueue[WorldNode], closedList: List[WorldNode], currentGScore: Int): List[WorldNode] = {

    if(openList.isEmpty) {
      println("NO PATH TO GOAL")
      return Nil
    }

    val currentNode = openList.dequeue()
    if(currentNode == world.goal) {
      println("SUCCESS")
      println("Required expansions: " + currentGScore.toString)
      currentNode.reconstructPath()
    }

    else {
      val newElements = world.getNeighbours(currentNode) diff closedList
      newElements.foreach(_.update(currentGScore+1, world.start, world.goal, currentNode))
      doEverything(new mutable.PriorityQueue[WorldNode]() ++= (openList.toSet ++ newElements),
        currentNode :: closedList,
        currentGScore+1)
    }
  }
}


class World(width: Int, height: Int, board: List[Char]) {

  val start: WorldNode = WorldNode(board.indexOf('s')%width, board.indexOf('s')/width.floor.toInt, false)
  val goal: WorldNode = WorldNode(board.indexOf('g')%width, board.indexOf('g')/width.floor.toInt, false)

  val nodes: List[WorldNode] = for {node <- board.zipWithIndex } yield
    if (node._1 == 'x') WorldNode(node._2%width, node._2/width.floor.toInt, true)
    else WorldNode(node._2%width, node._2/width.floor.toInt, false)

  for (node <- nodes) {
    node.distanceToGoal = Math.abs(goal.x - node.x) + Math.abs(goal.y - node.y)
  }

  def toString(path: List[WorldNode]): String = {
    val lala = StringBuilder.newBuilder
    for(node <- nodes){
      if (node.isWall) lala.append("x ")
      else if (node == goal) lala.append("g ")
      else if (path contains node) lala.append("o ")
      else if (node == start) lala.append("s ")
      else lala.append("_ ")
      if(node.x == width-1) lala.append("\n")
    }
    lala.toString()
  }

  def getNeighbours(node: WorldNode): List[WorldNode] = {
    nodes.intersect(List(WorldNode(node.x-1,node.y, false),
      WorldNode(node.x,node.y-1, false),
      WorldNode(node.x+1,node.y, false),
      WorldNode(node.x,node.y+1, false)))
  }
}

case class WorldNode(x: Int, y: Int, isWall: Boolean) extends Ordered[WorldNode] {
  var distanceFromStart: Int = 0 //g
  var distanceToGoal: Int = 0 //h
  var totalCost = 0.0 //f

  var parent:WorldNode = _

  def compare(that: WorldNode):Int = that.totalCost compare this.totalCost

  def update(newDistanceFromStart: Int, start: WorldNode, goal: WorldNode, parent: WorldNode): Unit = {

    this.parent = parent
    val dx1 = this.x - goal.x
    val dy1 = this.y - goal.y

    val dx2 = start.x - goal.x
    val dy2 = start.y - goal.y
    val cross = Math.abs(dx1*dy2 - dx2*dy1)
    if(distanceFromStart == 0) {
      distanceFromStart = newDistanceFromStart
      totalCost = distanceFromStart + distanceToGoal + cross*0.01 - newDistanceFromStart * 0.001
    }
  }

  def reconstructPath(): List[WorldNode] = {
    this.parent match {
      case null => Nil
      case parent1 => this :: parent1.reconstructPath()
    }
  }
}