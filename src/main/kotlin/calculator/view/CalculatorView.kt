package org.example.calculator.view

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.calculator.vm.CalculatorEffect
import org.example.calculator.vm.CalculatorIntent
import org.example.calculator.vm.CalculatorViewModel
import java.awt.*
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel

class CalculatorView : JFrame() {

    // 기본 프로그램 설정(제목, 크기, 위치)
    private val font: Font = Font("맑은 고딕", Font.BOLD, 30)
    private val expressFont: Font = Font("맑은 고딕", Font.PLAIN, 30)
    private val resultFont: Font = Font("맑은 고딕", Font.BOLD, 40)

    // 색깔
    private val gray: Color = Color(204, 204, 204)
    private val white: Color = Color(255, 255, 255)
    private val lightBlue: Color = Color(153, 153, 255)

    // vm
    val viewModel: CalculatorViewModel = CalculatorViewModel()

    init {
        setTitle("Calculator")
        setSize(350, 450)
        setLocation(800, 280)
        setDefaultCloseOperation(EXIT_ON_CLOSE)
        initView()
    }

    // 뷰 초기화 및 보여줌
    private fun initView() {

        // expression view
        val expression = JLabel("", JLabel.RIGHT).apply {
            alignmentX = RIGHT_ALIGNMENT
            font = expressFont
        }

        // display panel setup
        val result = JLabel("0", JLabel.RIGHT).apply {
            alignmentX = RIGHT_ALIGNMENT
            font = resultFont
        }
        val displayPanel = JPanel().apply {
            layout = GridLayout(2, 1)
            preferredSize = Dimension(290, 80) // 레이아웃 크기 설정
            add(expression)
            add(result)
        }

        val buttonPanel = JPanel().apply {
            layout = GridLayout(6, 4)
            preferredSize = Dimension(290, 300)
        }.also {
            // 만약 개선한다면 viewModel의 메소드를 바로 호출하는게 아니라, sendIntent 함수를 만들고, viewModel을 nullable하게 선언함.
            // 이후, viewModel이 null인경우는 전달 안되게, null이 아닌경우는 전달되게 하면 viewModel을 Caculator class에서 주입할 수 있을것 같음.
            // 첫번째 줄
            it.add(buildButton("%", onClick = { viewModel.sendIntent(CalculatorIntent.ModIntent)}))
            it.add(buildButton("CE", onClick = { viewModel.sendIntent(CalculatorIntent.ClearAllIntent)}))
            it.add(buildButton("C", onClick = { viewModel.sendIntent(CalculatorIntent.ClearCurrentInputIntent)}))
            it.add(buildButton("←", onClick = { viewModel.sendIntent(CalculatorIntent.DeleteOneNumberIntent) }))
            // 두번째 줄
            it.add(buildButton("¹/x", background = gray, onClick = { viewModel.sendIntent(CalculatorIntent.FractionIntent)}))
            it.add(buildButton("x²", background = gray, onClick = { viewModel.sendIntent(CalculatorIntent.SquaredIntent)}))
            it.add(buildButton("²√x", background = gray, onClick = { viewModel.sendIntent(CalculatorIntent.SqrtIntent)}))
            it.add(buildButton("÷", background = gray, onClick = { viewModel.sendIntent(CalculatorIntent.DivisionIntent)}))
            // 세번째 줄
            it.add(buildButton("7", background = white, font = font, onClick = { viewModel.sendIntent(CalculatorIntent.InputNumberIntent('7'))}))
            it.add(buildButton("8", background = white, font = font, onClick = { viewModel.sendIntent(CalculatorIntent.InputNumberIntent('8'))}))
            it.add(buildButton("9", background = white, font = font, onClick = { viewModel.sendIntent(CalculatorIntent.InputNumberIntent('9'))}))
            it.add(buildButton("×", background = gray, onClick = { viewModel.sendIntent(CalculatorIntent.MultiplyIntent)}))
            // 네번째 줄
            it.add(buildButton("4", background = white, font = font, onClick = { viewModel.sendIntent(CalculatorIntent.InputNumberIntent('4'))}))
            it.add(buildButton("5", background = white, font = font, onClick = { viewModel.sendIntent(CalculatorIntent.InputNumberIntent('5'))}))
            it.add(buildButton("6", background = white, font = font, onClick = { viewModel.sendIntent(CalculatorIntent.InputNumberIntent('6'))}))
            it.add(buildButton("-", background = gray, onClick = { viewModel.sendIntent(CalculatorIntent.MinusIntent)}))
            // 다섯번째 줄
            it.add(buildButton("1", background = white, font = font, onClick =  { viewModel.sendIntent(CalculatorIntent.InputNumberIntent('1'))}))
            it.add(buildButton("2", background = white, font = font, onClick = { viewModel.sendIntent(CalculatorIntent.InputNumberIntent('2'))}))
            it.add(buildButton("3", background = white, font = font, onClick = { viewModel.sendIntent(CalculatorIntent.InputNumberIntent('3'))}))
            it.add(buildButton("+", background = gray, onClick = { viewModel.sendIntent(CalculatorIntent.PlusIntent)}))
            // 여섯번째 줄
            it.add(buildButton("＋／－", background = gray, onClick = { viewModel.sendIntent(CalculatorIntent.InverseNumberIntent)}))
            it.add(buildButton("0", background = white, font = font, onClick = { viewModel.sendIntent(CalculatorIntent.InputNumberIntent('0'))}))
            it.add(buildButton(".", background = gray, onClick = { viewModel.sendIntent(CalculatorIntent.InputNumberIntent('.'))}))
            it.add(buildButton("=", background = lightBlue, onClick = { viewModel.sendIntent(CalculatorIntent.CalculateIntent)}))
        }


        // Main Panel 선언
        val mainPanel = JPanel().apply {
            layout = FlowLayout()
        }
        mainPanel.add(displayPanel)
        mainPanel.add(buttonPanel)
        this.add(mainPanel)

        // observe
        viewModel.state.onEach {
            // result
            result.text = it.currentInput
            // exp
            expression.text = it.previousOperation.joinToString(separator = "")
        }.launchIn(CoroutineScope(Dispatchers.Default))

        viewModel.effect.onEach {
            when(it) {
                is CalculatorEffect.CreateAlert -> JOptionPane.showMessageDialog(mainPanel, it.message, "경고", JOptionPane.WARNING_MESSAGE)
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))

    }


    fun showCalculator() {
        // 창 보이기 설정
        isVisible = true
    }


    // 계산기 버튼 빌드
    private fun buildButton(
        text: String,
        background: Color? = null,
        font: Font? = null,
        onClick: ActionListener? = null
    ): JButton {
        return JButton(text).apply {
            if (background != null)
                this.background = background
            if (font != null)
                this.font = font
            addActionListener(onClick)
        }
    }
}