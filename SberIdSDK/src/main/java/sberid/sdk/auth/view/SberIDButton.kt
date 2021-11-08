/*
 * Используя данное SDK вы соглашаетесь с Пользовательским соглашением, размещенным по адресу:
 * https://www.sberbank.ru/common/img/uploaded/files/sberbank_id/agreement_sdk.pdf
 */
package sberid.sdk.auth.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.marginEnd
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginStart
import androidx.core.widget.TextViewCompat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import sberid.sdk.auth.R
import sberid.sdk.auth.SdkConstant
import sberid.sdk.auth.analytics.ISberIDAnalyticsPlugin
import sberid.sdk.auth.analytics.SberIDAnalyticsPluginProvider
import sberid.sdk.auth.model.SberIDButtonDesignModel
import sberid.sdk.auth.service.CustomTabsServiceConnector
import sberid.sdk.auth.service.PersonalDataServiceConnector
import sberid.sdk.auth.service.ServiceConnectionWrapper
import sberid.sdk.auth.service.StubPersonalDataServiceConnector
import sberid.sdk.auth.utils.SberIDCustomTabsUtils
import sberid.sdk.auth.view.SberIDButton.Companion.ButtonBackground.DEFAULT
import sberid.sdk.auth.view.SberIDButton.Companion.ButtonBackground.WHITE_COLOR
import sberid.sdk.auth.view.SberIDButton.Companion.ButtonText.CONTINUE
import sberid.sdk.auth.view.SberIDButton.Companion.ButtonText.FILL
import sberid.sdk.auth.view.SberIDButton.Companion.ButtonText.LOGIN
import sberid.sdk.auth.view.SberIDButton.Companion.ButtonText.LOGIN_SHORT

/**
 * Стандартная реализация кнопки для входа через Сбер ID
 * <p>
 *     Кнопка сама подставляет иконку Сбербанка, текст и его стиль.
 *
 *     Высота кнопки органичена интервалом 28dp-64dp, при установке высоты, выходящей за пределы интервала,
 *     высота будет автоматически приведена в соответствующей границе
 *     Ширина кнопки имеет мимнимальное значение, исходя из размеров выбранного текста, логотипа и отступов
 *     Максимальная ширина кнопки не ограничена
 *
 *     По умолчанию кнопка отображается без скруглений углов, для изменения скругления кнопки
 *     укажите в xml атрибут кнопки app:buttonCornerRadius, можно выбрать значения
 *     corner_small, corner_large или указать собственное закругление
 *
 * </p>
 * <p>
 *     Цвет текста/фона кнопки и сам текст определяется атрибутами
 *     - app:buttonType (значения white_type и default_type), если атрибут не указать, кнопка будет по умолчанию иметь тип default_type
 *     - app:buttonText
 *       Соответствие значений атрибутов и текста:
 *          login_short - Сбер ID
 *          login - Войти по Сбер ID
 *          continue_on - Продолжить cо Сбер ID
 *          fill - Заполнить cо Сбер ID
 *       Если атрибут не указать, кнопка по умлочанию будет иметь текст, соответствующий значению login
 *       Указать другой текст на кнопке нельзя
 *
 *     Иконка Сбербанка автоматически меняет свой цвет и размер в зависимости от значения атрибута app:buttonType
 *     и установленной высоты кнопки
 *
 *      Для кнопки с типом white_type существует возможность указать свой цвет окантовки через атрибут app:buttonStrokeColor
 *      Формат значения - color
 *      По умолчанию для кнопки с типом white_type значение цвета окантовки #767676
 *      Для кнопки с типом default_type установка атрибута app:buttonStrokeColor будет проигнорирована
 *
 *     allCaps всегда выключен (т.е. текст не будет заглавными буквами)
 * </p>
 * <p>
 *     Пример использования кнопки (зеленая кнопка без скруглений с текстом Войти по Сбер ID высотой 40dp и отступами 36dp):
 *     <sberid.sdk.auth.view.SberIDButton
 *        android:id="@+id/sberid_logo"
 *        android:layout_width="match_parent"
 *        android:layout_height="40dp"
 *        android:layout_margin="36dp"/>
 *
 *     Пример использования кнопки (белая кнопка c малым скруглением с текстом Сбер ID высотой 30dp, минимальной шириной и кастомной окантовкой):
 *     <sberid.sdk.auth.view.SberIDButton
 *        android:id="@+id/sberid_logo"
 *        android:layout_width="wrap_content"
 *        android:layout_height="30dp"
 *        app:buttonType="white_type"
 *        app:buttonText="login_short"
 *        app:buttonCornerRadius="small"
 *        app:buttonStrokeColor="@color/colorAccent"/>
 * </p>
 *
 * @author Gerasimenko Nikita
 * @author Lelyukh Aleksandr
 */
class SberIDButton : FrameLayout {

    companion object {

        private const val TAG = "SberIDButton"

        private const val DEFAULT_RADIUS_BUTTON = 0f

        private const val STROKE_WIDTH = 3

        private const val ROBOTO_TYPEFACE = "fonts/Roboto-Regular.ttf"

        private const val WIDTH_ERROR_TAG = "SberIDButtonWidthError"

        private const val TEXT_SIZE_SMALL = 14F

        private const val TEXT_SIZE_LARGE = 16F

        /**
         * Содержит информацию о стандартных стилях
         * @property buttonType тип кнопки
         */
        private enum class ButtonBackground(val buttonType: Int) {
            DEFAULT(0),
            WHITE_COLOR(1)
        }

        /**
         * Содержит информацию о стандартных текстовках кнопки
         * @property textType тип названия кнопки
         * @property resID ресурс с текстом
         */
        private enum class ButtonText(val textType: Int, val resID: Int) {
            LOGIN(0, R.string.login_sber_id_logo_text),
            LOGIN_SHORT(1, R.string.login_short_sber_id_logo_text),
            CONTINUE(2, R.string.continue_sber_id_logo_text),
            FILL(3, R.string.fill_sber_id_logo_text),
        }
    }

    /**
     * Признак показа кнопки входа по  Сбер ID, нужен для избежания дублирования
     * отправки событий в аналитику при показе именной кнопки
     */
    private var isSberIDButtonShown = false

    /**
     * Использовать конкретные реализации [ServiceConnectionWrapper] по назначению:
     * [PersonalDataServiceConnector]       - базовый сервис, персонализация включена
     * [StubPersonalDataServiceConnector]   - сервис-заглушка, персонализация отключена
     */
    private val personalDataServiceConnector: ServiceConnectionWrapper =
        PersonalDataServiceConnector(this)

    private var customTabsServiceConnection: CustomTabsServiceConnector? = null
    private var sberIDLoginButtonWidth: Int = 0
    private var displayMetrics = DisplayMetrics()

    private lateinit var sberIDLoginTextView: TextView
    private lateinit var styleAttributes: TypedArray

    private val designModel: SberIDButtonDesignModel = SberIDButtonDesignModel()
    private val analyticsPlugin: ISberIDAnalyticsPlugin = SberIDAnalyticsPluginProvider.getInstance()
    private val logoPadding = resources.getDimensionPixelSize(R.dimen.sber_logo_padding)

    private var isLoading = false
    private var isPersonalizationStart = false
    private var isPersonalizationComplete = false

    private var maskName: String? = null
    private var sberIDLoginButtonText: CharSequence? = null

    /**
     * Используется для рестарта анимации, при onDetachedFromWindow удаляется.
     * Иначе приводит к фризам анимации, при повторном открытии
     */
    private val reStartAnimRun = Runnable {
        if (sberIDLoginTextView.compoundDrawables[0] != null) {
            (sberIDLoginTextView.compoundDrawables[0] as Animatable).start()
        }
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs)
    }

    /**
     * Расчет корректных размеров высоты и ширины кнопки, порядок вызовом методов имеет значение.
     * super.onMeasure() должен выполняться в конце, когда все спецификации рассчитаны,
     * метод initLogoAndTextWithCorrectSize() должен выполняться до создания спецификации высоты,
     * которая
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val correctHeight = getCorrectButtonHeight(heightSpecSize)

        initLogoAndTextWithCorrectSize(styleAttributes, correctHeight, widthSpecSize)

        val newWidthMeasureSpec =
            MeasureSpec.makeMeasureSpec(
                sberIDLoginButtonWidth,
                MeasureSpec.EXACTLY
            )

        val newHeightMeasureSpec =
            MeasureSpec.makeMeasureSpec(
                correctHeight,
                MeasureSpec.EXACTLY
            )

        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if ((isLoading || personalDataServiceConnector.isBound) || sberIDLoginTextView.text.isEmpty()) {
            startLoaderAnimationLogo(sberIDLoginTextView.compoundDrawables[0])
        }
        sendShowButtonEvents()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        SberIDAnalyticsPluginProvider.release()
        stopLoadingMaskData()
        isLoading = false
        isSberIDButtonShown = false
        handler.removeCallbacks(reStartAnimRun)
        disconnectCustomTabsService()
    }

    /**
     * Установить актуальный статус индикатора загрузки в кнопке
     * В слуачае установки статуса загрузки стартует анимация, текст в кнопке зануляется
     * В случае отмены статуса загрузки анимация останавливается, в кнопке отображается
     * стандартный/персонализированный текст, либо продолжается анимация до завершения работы процедуры персонализации
     *
     * @param isLoading - true, если необходимо отобразить на кнопке статус загрузки, false - в противном случае
     */
    fun setLoaderState(isLoading: Boolean) {
        this.isLoading = isLoading
        setEnableState()
        val sberLogo = sberIDLoginTextView.compoundDrawables[0]
        if (this.isLoading) {
            TransitionManager.beginDelayedTransition(this)
            startLoaderAnimationLogo(sberLogo)
            sberIDLoginTextView.text = null
            sberIDLoginTextView.compoundDrawablePadding = 0
        }
    }

    /**
     * Метод для инициализации названия кнопки маскированным именем, после название
     * станет вида "Войти как Имя Ф."
     * <p>
     *     Отображение кнопки происходит по окончанию анимации загрузки, смотри 'startAnimationLogo'
     *   <p>
     *     В сценарии с неклиентами сохраняется " null", поэтому добавил проверку
     *
     * @param name маскированное имя пользователя
     */
    internal fun initMaskNameText(name: String?) {
        stopLoadingMaskData()
        if (!name.isNullOrBlank() && name.trim() != "null") {
            maskName = name
            designModel.isPersonal = true
        }
    }

    private fun initView(context: Context) {
        initView(context, null)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        try { // Для случаев, когда партнер не подкючил библиотеку androidx.browser
            customTabsServiceConnection = SberIDCustomTabsUtils.warnUpCustomTabs(context.applicationContext)
        } catch (e: Throwable) {
            Log.e(TAG, e.message.toString(), e)
        }
        LayoutInflater.from(context).inflate(R.layout.sber_id_button_layout, this)
        styleAttributes = context.obtainStyledAttributes(
            attrs, R.styleable.SberIDButton,
            0, 0
        )
        startLoadingMaskData()
        sberIDLoginTextView = findViewById(R.id.sber_id_login_text_view)

        initBackground(styleAttributes)
        initDisplayMetrics()
        initText(styleAttributes)
        analyticsPlugin.initPlugin(context.applicationContext)
    }

    /**
     * Вычислить корректную высоту кнопки
     * Минимально допустимое значение 28dp, максимальное 64dp
     * Если кто-то захотел установить высоту кнопки wrap_content, она будет иметь
     * минимальную высоту независимо от высоты, установленной в родительском контейнере
     *
     * @param heightSpecSize высота из спецификации, взятая из атрибутов в разметке
     *
     * @return корректная высота кнопки
     */
    private fun getCorrectButtonHeight(heightSpecSize: Int): Int {
        val minHeight = resources.getDimensionPixelSize(R.dimen.min_height)
        val maxHeight = resources.getDimensionPixelSize(R.dimen.max_height)
        val correctHeight =
            if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) minHeight
            else minOf(maxOf(heightSpecSize, minHeight), maxHeight)
        designModel.height = correctHeight
        return correctHeight
    }

    /**
     * Вычислить корректную ширину кнопки
     * Минимально допустимое значение = ширина текста + 3 ширины лого + отступ между текстом и лого,
     * если условие нарушено, минимальная ширина будет установлена принудительно
     * Максимальное не ограничено
     * Если кто-то захотел установить ширину кнопки wrap_content, она будет иметь
     * минимально возможную ширину независимо от высоты, установленной в родительском контейнере
     */
    private fun calcCorrectButtonWidth(logoWidth: Int, textWidth: Int, buttonWidth: Int) {
        val minButtonWidth = textWidth
            .plus(logoWidth.times(3))
            .plus(logoPadding)
        checkMatchParentMargins(minButtonWidth)
        sberIDLoginButtonWidth = maxOf(
            if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) minButtonWidth else buttonWidth,
            minButtonWidth
        )
        designModel.width = sberIDLoginButtonWidth
    }

    /**
     * Проверка установки слишком больших отступов при ширине match_parent
     * Если итоговая ширина кнопки с учетом отступов меньше рассчитанной, будет попытка установки минимальной ширины
     * Если отступы выставлялись не на кнопке, а в родительских вью, проверка не сработает и размер не увеличит
     * В этом случае при отрисовке кнопки сработает проверка в методе sendShowButtonEvents,
     * напишется сообщение об ошибке в лог и отправится сообытие в аналитику (если включен плагин)
     */
    private fun checkMatchParentMargins(minButtonWidth: Int) {
        val params = layoutParams
        if (params.width == ViewGroup.LayoutParams.MATCH_PARENT
            || params.width == ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
        ) {
            val currentOffset = maxOf(marginStart, marginLeft)
                .plus(maxOf(marginEnd, marginRight))
            val maxMeasuredEmptySpace = displayMetrics.widthPixels.minus(minButtonWidth)
            if (currentOffset > maxMeasuredEmptySpace) {
                params.width = minButtonWidth
                layoutParams = params
            }
        }
    }

    private fun initBackground(styleAttributes: TypedArray) {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadius = styleAttributes.getDimension(
            R.styleable.SberIDButton_buttonCornerRadius,
            DEFAULT_RADIUS_BUTTON
        )
        when (styleAttributes.getInt(R.styleable.SberIDButton_buttonType, DEFAULT.buttonType)) {
            WHITE_COLOR.buttonType -> {
                shape.setColor(
                    ContextCompat.getColor(context, android.R.color.transparent)
                )
                val strokeColor = styleAttributes.getColor(
                    R.styleable.SberIDButton_buttonStrokeColor,
                    ContextCompat.getColor(context, R.color.color_sber_id_button_grey)
                )
                shape.setStroke(STROKE_WIDTH, strokeColor)
                designModel.isColored = false
            }
            else -> {
                shape.setColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_sber_id_button_primary
                    )
                )
                designModel.isColored = true
            }
        }
        background = shape
    }

    /**
     * Инициализировать метрики дисплея
     * В 30-м API метод [android.view.WindowManager.getDefaultDisplay] помечен как Deprecated,
     * рекомендовано получать их через [android.content.Context.getDisplay], но так как он
     * может возвращать null, в конце на всякий случай возьмем метрики из ресурсов (откуда их
     * документация использовать не советует)
     * Также для API<30 метрики будут взяты из ресурсов, если текущий контекст не является активити
     */
    private fun initDisplayMetrics() {
        if (Build.VERSION.SDK_INT >= 30) {
            context.display?.getRealMetrics(displayMetrics)
        } else if (context is Activity) {
            (context as Activity).windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        }
        if (displayMetrics.widthPixels == 0) {
            displayMetrics = resources.displayMetrics
        }
    }

    private fun initText(styleAttributes: TypedArray) {
        sberIDLoginTextView.typeface = Typeface.createFromAsset(context.assets, ROBOTO_TYPEFACE)
        when (styleAttributes.getInt(R.styleable.SberIDButton_buttonType, DEFAULT.buttonType)) {
            WHITE_COLOR.buttonType -> {
                TextViewCompat.setTextAppearance(sberIDLoginTextView, android.R.style.TextAppearance)
            }
            else -> {
                sberIDLoginTextView.setTextColor(
                    ContextCompat.getColor(context, R.color.color_sber_id_button_white)
                )
            }
        }
        sberIDLoginTextView.alpha = 1f
        sberIDLoginTextView.isAllCaps = false

        sberIDLoginButtonText = getResourcesText(styleAttributes)
        contentDescription = String.format(
            resources.getString(R.string.talkback_click_text), sberIDLoginButtonText
        )
        isLoading = styleAttributes.getBoolean(R.styleable.SberIDButton_buttonLoader, false)
        setEnableState()
    }

    private fun setEnableState() {
        isEnabled = !isLoading
    }

    private fun setTextIfNeeded() {
        if (!isLoading && !personalDataServiceConnector.isBound) {
            TransitionManager.beginDelayedTransition(this)
            sberIDLoginTextView.compoundDrawablePadding = logoPadding
            sberIDLoginTextView.text = sberIDLoginButtonText
        }
    }

    private fun getResourcesText(styleAttributes: TypedArray): CharSequence? {
        return when (styleAttributes.getInt(R.styleable.SberIDButton_buttonText, LOGIN.textType)) {
            LOGIN_SHORT.textType -> resources.getString(LOGIN_SHORT.resID)
            CONTINUE.textType -> resources.getString(CONTINUE.resID)
            FILL.textType -> resources.getString(FILL.resID)
            else -> resources.getString(LOGIN.resID)
        }
    }

    /**
     * Проверяем возможность показа именной кнопки, сейчас ограничиваемся самым коротким вариантом
     * кнопки, текст которой "Sber ID" [LOGIN_SHORT]
     */
    private fun isPersonalButtonEnabled(): Boolean {
        return styleAttributes.getInt(
            R.styleable.SberIDButton_buttonText, LOGIN.textType
        ) != LOGIN_SHORT.textType
    }

    private fun initLogoAndTextWithCorrectSize(
        styleAttributes: TypedArray,
        height: Int,
        width: Int
    ) {
        val sberLogo = createLogoAndSetSize(styleAttributes, height, width)
        if (sberIDLoginTextView.compoundDrawables[0] == null) {
            sberIDLoginTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                sberLogo,
                null,
                null,
                null
            )
        }
    }

    /**
     * Старт бесконечной анимации лого для ожидания окончания работы процедуры персонализации кнопки или при установке
     * статуса загрузки
     *
     * post перед стартом и инициалтзацией слушателя окончания сессии был добавлен для поддержки
     * старых версий Android 6.0 и ниже.
     * Эти методы добавляют в обще=ий пул действия, в итоге не произойдет ситуации, когда анимация
     * запущена, а ui не отрисовался(я понял проблема в этом). Если не использовать появляются
     * фризы анимации
     */
    private fun startLoaderAnimationLogo(sberLogo: Drawable?) {
        AnimatedVectorDrawableCompat.registerAnimationCallback(
            sberLogo,
            object : Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawablew: Drawable?) {
                    if (isLoading || personalDataServiceConnector.isBound) {
                        handler?.post(reStartAnimRun)
                    } else {
                        setMaskName()
                    }
                }
            })
        handler?.post(reStartAnimRun)
    }

    private fun sendShowButtonEvents() {
        if (!isSberIDButtonShown) {
            if (!personalDataServiceConnector.isBound) {
                isSberIDButtonShown = true
                analyticsPlugin.sberIDButtonShow(designModel)
            }
            val measuredWidth = designModel.width
            if (width < measuredWidth) {
                analyticsPlugin.sberIDWrongButtonSize(measuredWidth, width)
                Log.e(
                    WIDTH_ERROR_TAG, "Нарушена минимально допустимая ширина кнопки! "
                        + "Реальная ширина " + width.div(displayMetrics.density)
                        .toInt() + "dp, "
                        + "минимальная ширина " + measuredWidth.div(displayMetrics.density)
                        .toInt() + "dp"
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setMaskName() {
        if (maskName != null) {
            val currentLength = sberIDLoginButtonText.toString().length

            sberIDLoginButtonText =
                if (maskName?.length!! > currentLength) maskName
                else resources.getString(R.string.login_as_text) + maskName

            contentDescription = String.format(
                resources.getString(R.string.talkback_click_text),
                resources.getString(R.string.login_as_text) + maskName
            )
        }
        isPersonalizationComplete = true
        setTextIfNeeded()
    }

    private fun createLogoAndSetSize(
        styleAttributes: TypedArray,
        heightButton: Int,
        widthButton: Int
    ): Drawable? {
        val mediumHeight = resources.getDimensionPixelSize(R.dimen.medium_border_height)
        val largeHeight = resources.getDimensionPixelSize(R.dimen.large_border_height)

        var textSize = TEXT_SIZE_SMALL

        val drawable = AppCompatResources.getDrawable(
            context,
            when {
                heightButton < mediumHeight -> R.drawable.ic_anim_sber_logo_16dp
                heightButton in mediumHeight until largeHeight -> R.drawable.ic_anim_sber_logo_22dp
                else -> {
                    textSize = TEXT_SIZE_LARGE
                    R.drawable.ic_anim_sber_logo_26dp
                }
            }
        )

        when (styleAttributes.getInt(R.styleable.SberIDButton_buttonType, DEFAULT.buttonType)) {
            WHITE_COLOR.buttonType -> {
                DrawableCompat.setTint(
                    drawable!!.mutate(),
                    ContextCompat.getColor(context, R.color.color_sber_id_button_primary)
                )
            }
            else -> {
                DrawableCompat.setTint(
                    drawable!!.mutate(),
                    ContextCompat.getColor(context, R.color.color_sber_id_button_white)
                )
            }
        }

        sberIDLoginTextView.textSize = textSize
        //TODO Вынести инициализацию иконки и установку размеров в разные методы SID-5561
        setSberIDLoginWidth(drawable, widthButton)

        return drawable
    }

    /**
     * Метод устанавливает правильный размер кнопки - обводки и максимальный размер текста.
     * Размеры определяются на основе компонента 'app:buttonText' и текста из ресурсов, если
     * кнопки версталась с названием Сбер ID размер кнопки будет не больше этого размера, и
     * именная кнопка уже будет обрезана под этот размер.
     * <p>
     * 'mSberIDLoginTextView?.maxWidth' указывается как раз для того, чтобы обрезать текст именной
     * кнопки под первоначальные размеры, чтобы не нарушать общую верстку виджета. Размер текста
     * собира из падинга между иконкой и текстом + первоночальный текст из ресурсов + иконка
     *
     * @param drawable иконка кнопки
     */
    private fun setSberIDLoginWidth(drawable: Drawable, buttonWidth: Int) {
        val textWidth = sberIDLoginTextView
            .paint
            ?.measureText(getResourcesText(styleAttributes).toString())?.toInt() ?: 0

        calcCorrectButtonWidth(drawable.intrinsicWidth, textWidth, buttonWidth)

        sberIDLoginTextView.maxWidth =
            maxOf(width, sberIDLoginButtonWidth)
                .minus(drawable.intrinsicWidth.times(2))
    }

    /**
     * 'isBound' выставляется сразу для того, чтобы сразу получить информации есть ли возможность
     * подключения к сервису. Если такой возможности нет, тогда мы сразу будем рисовать статичный
     * текст и не показывать анимацию загрузки.
     * Дополнительно сохраняется признак старта процесса персонализации, чтобы обработать сценарии, когда
     * процесс стартовал, но еще не завершился
     * Привязка сервиса будет запущена только для базовой реализации [PersonalDataServiceConnector]
     */
    private fun startLoadingMaskData() {
        val intent = Intent()
        intent.`package` = SdkConstant.PACKAGE_SBOL
        intent.action = "ru.sberbank.mobile.sberid.MASK_NAME"
        if (isPersonalButtonEnabled() && personalDataServiceConnector is PersonalDataServiceConnector) {
            try {
                personalDataServiceConnector.isBound = context.applicationContext.bindService(
                    intent, personalDataServiceConnector,
                    Context.BIND_AUTO_CREATE
                )
            } catch (e: SecurityException) {
                //Ловим на 10 Android (Vision1Pro) ошибку
                Log.e(TAG, "startLoadingMaskData: ", e)
            }
            isPersonalizationStart = personalDataServiceConnector.isBound
        }
    }

    private fun stopLoadingMaskData() {
        post {
            if (personalDataServiceConnector.isBound) {
                personalDataServiceConnector.isBound = false
                context.applicationContext.unbindService(personalDataServiceConnector)
            }
        }
    }

    private fun disconnectCustomTabsService() {
        customTabsServiceConnection?.let { serviceConnector ->
            {
                if (serviceConnector.isBound) {
                    serviceConnector.isBound = false
                    context.applicationContext.unbindService(serviceConnector)
                }
            }
        }
    }
}