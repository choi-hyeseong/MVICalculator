package org.example.calculator.view

import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel

class CalculatorView : JFrame() {

    // 기본 프로그램 설정(제목, 크기, 위치)
    private val font: Font = Font("맑은 고딕", Font.BOLD, 30)
    private val resultFont: Font = Font("맑은 고딕", Font.BOLD, 40)

    // 색깔
    private val gray: Color = Color(204, 204, 204)
    private val white: Color = Color(255, 255, 255)
    private val lightBlue: Color = Color(153, 153, 255)

    init {
        setTitle("Calculator")
        setSize(350, 450)
        setLocation(800, 280)
        setDefaultCloseOperation(EXIT_ON_CLOSE)
        initView()
    }

    // 뷰 초기화 및 보여줌
    private fun initView() {

        // display panel setup
        val result = JLabel("0", JLabel.RIGHT).apply {
            alignmentX = RIGHT_ALIGNMENT
            font = resultFont
        }
        val displayPanel = JPanel().apply {
            layout = GridLayout(2, 1)
            preferredSize = Dimension(290, 80) // 레이아웃 크기 설정
            add(result)
        }

        val buttonPanel = JPanel().apply {
            layout = GridLayout(6, 4)
            preferredSize = Dimension(290, 300)
        }.also {
            // 첫번째 줄
            it.add(buildButton("%"))
            it.add(buildButton("CE"))
            it.add(buildButton("C") {
                result.text = "0"
            })
            it.add(buildButton("←", onClick = buildDeleteOneAction(result)))
            // 두번째 줄
            it.add(buildButton("¹/x", background = gray))
            it.add(buildButton("x²", background = gray))
            it.add(buildButton("²√x", background = gray))
            it.add(buildButton("÷", background = gray))
            // 세번째 줄
            it.add(buildButton("7", background = white, font = font, onClick = buildDialAction(result, '7')))
            it.add(buildButton("8", background = white, font = font, onClick = buildDialAction(result, '8')))
            it.add(buildButton("9", background = white, font = font, onClick = buildDialAction(result, '9')))
            it.add(buildButton("×", background = gray))
            // 네번째 줄
            it.add(buildButton("4", background = white, font = font, onClick = buildDialAction(result, '4')))
            it.add(buildButton("5", background = white, font = font, onClick = buildDialAction(result, '5')))
            it.add(buildButton("6", background = white, font = font, onClick = buildDialAction(result, '6')))
            it.add(buildButton("-", background = gray))
            // 다섯번째 줄
            it.add(buildButton("1", background = white, font = font, onClick = buildDialAction(result, '1')))
            it.add(buildButton("2", background = white, font = font, onClick = buildDialAction(result, '2')))
            it.add(buildButton("3", background = white, font = font, onClick = buildDialAction(result, '3')))
            it.add(buildButton("+", background = gray))
            // 여섯번째 줄
            it.add(buildButton("＋／－", background = gray, onClick = buildInverseAction(result)))
            it.add(buildButton("0", background = white, font = font, onClick = buildDialAction(result, '0')))
            it.add(buildButton(".", background = gray, onClick = buildDialAction(result, '.')))
            it.add(buildButton("=", background = lightBlue))
        }


        // Main Panel 선언
        val mainPanel = JPanel().apply {
            layout = FlowLayout()
        }
        mainPanel.add(displayPanel)
        mainPanel.add(buttonPanel)
        this.add(mainPanel)
    }

    fun showCalculator() {
        // 창 보이기 설정
        isVisible = true
    }
    /**
     * Button Action implement part.
     * 필요시 클래스단위로 구현해서 상속 or 구현 구조로 짜도 될듯
     */
    // 입력값 한개 삭제 구현
    private fun buildDeleteOneAction(label: JLabel) : ActionListener {
        return ActionListener {
            val text = label.text
            // 입력값이 없는경우 리턴
            if (label.isInputEmpty())
                return@ActionListener
            label.text = label.text.substring(0, text.length - 1)

            // 부호부 삭제
            if (label.text == "-")
                label.text = "0"
        }
    }


    // 입력값 반전 버튼 구현
    private fun buildInverseAction(label: JLabel) : ActionListener {
        return ActionListener {
            val text = label.text
            // 입력값이 없는경우 리턴
            if (label.isInputEmpty())
                return@ActionListener

            // 반전
            if (text.startsWith("-"))
                label.text = text.substring(1)
            else
                label.text = "-$text"
        }
    }

    // 숫자 버튼 클릭시 뷰에 띄우기
    private fun buildDialAction(label : JLabel, input : Char) : ActionListener {

        return ActionListener {
            // 점이 있는경우 추가 안되게
            if (label.text.contains(".") && input == '.')
                return@ActionListener

            // 아무 값도 없는 초기인 경우 및 입력값이 소수점이 아닌경우
            if (label.isInputEmpty() && input != '.')
                label.text = input.toString()
            else
                label.text += input.toString()
        }
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

// 입력값이 비었는지 확인 - 확장 함수
fun JLabel.isInputEmpty() : Boolean = this.text.isEmpty() || this.text == "0"

