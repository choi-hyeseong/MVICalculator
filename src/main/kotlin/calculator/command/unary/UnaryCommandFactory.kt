package org.example.calculator.command.unary

class UnaryCommandFactory private constructor() {

    companion object {
        val instance : UnaryCommandFactory = UnaryCommandFactory()
    }

    fun provide(operation : String, input : Double) : UnaryOperationCommand {
        return when (operation) {
            "¹/x" -> FractionOperation(input)
            "x²" -> SquareOperation(input)
            "²√x" -> SqrtOperation(input)
            else -> throw IllegalArgumentException("not supported")
        }
    }


}