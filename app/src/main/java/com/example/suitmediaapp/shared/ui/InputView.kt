package com.example.suitmediaapp.shared.ui

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.text.*
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.suitmediaapp.R
import com.example.suitmediaapp.databinding.ItemInputBinding
import com.example.suitmediaapp.shared.ext.currencyFormatter
import com.example.suitmediaapp.shared.ext.getStringRes
import com.example.suitmediaapp.shared.ext.setVisible

class InputView: ConstraintLayout {

    companion object {
        val TAG: String = InputView::class.java.simpleName
    }

    /**
     * Variables
     */
    //State
    private lateinit var mContext: Context
    private lateinit var binding: ItemInputBinding

    enum class TYPE { TEXT, PASSWORD, PASSWORD_NUMBER, NUMBER, EMAIL, DISABLE, CURRENCY, MENU, PHONE_OR_EMAIL, FIELD_NO_SUGGESTION }
    enum class ACTION { NEXT, DONE }
    enum class MASKING_TYPE { LOGIN_FIELD, CGV }

    private var inputType: TYPE = TYPE.TEXT
    private var listener: InputListener? = null
    private var isDeleteMode: Boolean = false
    private var isClick: Boolean = false
    private var currentCurrency: String = ""
    private var limitCounter: String = ""
    private var listSpinner: ArrayList<String> = ArrayList()
    private var isFieldError: Boolean = false
    private var isMasking: Boolean = false
    private var maskingType: MASKING_TYPE? = null
    private var plainText: String = ""
    private var isClearTextEnable = false

    /**
     * Constructors
     */
    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    /**
     * Initialization
     */
    private fun init(context: Context, attributeSet: AttributeSet?) {
        mContext = context

        //Inflate view
        binding = ItemInputBinding.bind(
            LayoutInflater.from(mContext).inflate(R.layout.item_input, this, true)
        )

        binding.txtInput.addTextChangedListener(textWatcher)
        binding.cbPassword.setOnCheckedChangeListener { _, isChecked ->
            binding.txtInput.transformationMethod =
                if (isChecked) PasswordTransformationMethod()
                else HideReturnsTransformationMethod.getInstance()
            binding.txtInput.setSelection(binding.txtInput.text.toString().length)
        }
        binding.cbPassword.isChecked = true
        binding.lblSuffix.setOnClickListener {
            listener?.onSuffixClick()
            if (listSpinner.size > 0) binding.spinnerSuffix.performClick()
        }
        binding.imgInputType.setOnClickListener { listener?.onInputIconClick() }
        binding.constInput.setOnClickListener { if (inputType == TYPE.DISABLE) listener?.onInputClick() }
        binding.lblInputValue.setOnClickListener { if (inputType == TYPE.DISABLE) listener?.onInputClick() }
        binding.imgInputType.setOnClickListener { if (inputType == TYPE.DISABLE) listener?.onInputClick() }
    }


    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isMasking) {
                Log.d(TAG, "s: $s, start: $start, before: $before, count: $count")
                plainText = when {
                    count > before -> {
                        val builder = StringBuilder()
                        builder.append(plainText)
                        builder.append(s?.takeLast(count - before))
                        builder.toString()
                    }
                    start != 0 -> plainText.take(plainText.length - 1)
                    else -> s.toString()
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
            if (inputType == TYPE.CURRENCY) {
                if (s.toString() != currentCurrency) {
                    binding.txtInput.removeTextChangedListener(this)

                    val cleanString = s.toString().replace(",", "").replace(".", "")
                    var formatted = ""
                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toDouble()
                        formatted = parsed.currencyFormatter()
                        currentCurrency = formatted
                    } else {
                        currentCurrency = ""
                    }

                    binding.txtInput.setText(formatted)
                    binding.txtInput.setSelection(formatted.length)
                    binding.txtInput.addTextChangedListener(this)
                }
            }

            binding.lblInput.isSelected = binding.txtInput.text.toString().isNotEmpty()
            binding.constInput.isSelected = binding.txtInput.text.toString().isNotEmpty()
            if (isDeleteMode) binding.imgInputType.setVisible(
                binding.txtInput.text.toString().isNotEmpty()
            )
            if (limitCounter.isNotEmpty())
                binding.lblAlert.text = String.format(
                    context.getStringRes(R.string.required),
                    binding.txtInput.text.toString().length, limitCounter
                )
            if (isMasking) {
                binding.txtInput.removeTextChangedListener(this)
                val masked = maskText(plainText)
                binding.txtInput.text.clear()
                binding.txtInput.append(masked)
                binding.txtInput.setSelection(masked.length)
                binding.txtInput.addTextChangedListener(this)
                Log.d(TAG, "raw: $plainText")
                Log.d(TAG, "masked: $masked")
            }
            // listener
            listener?.afterTextChanged()

            if (isClearTextEnable) showClearText(getText().isNotBlank())
        }
    }

    fun setMasking(isMasking: Boolean, type: MASKING_TYPE) {
        this.isMasking = isMasking
        maskingType = type
    }

    fun disableCopyCutShare(activity: Activity) {
//        binding.txtInput.isLongClickable = false

        binding.txtInput.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {

                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return true
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                if (item?.itemId == 16908320 || item?.itemId == 16908321 || item?.itemId == 16908341 || item?.itemId == 16908353) {
                    val cb = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("copy", "")
                    cb.setPrimaryClip(clip)
                }
                return true
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
            }

        }
    }

    fun setMasking2(start: Int, end: Int) {
        isMasking = true
    }

    fun setTitle(title: String) {
        binding.lblInput.text = title
    }

    fun setTitleStyle(resId: Int) {
        binding.lblInput.setTextAppearance(resId)
    }

    fun setVisibleTitle(view: Int) {
        binding.lblInput.visibility = view
    }

    fun setHint(hint: String) {
        binding.lblInputValue.text = hint
        binding.lblInputValue.setTextAppearance(R.style.TextView_Regular14spGrey)
        binding.txtInput.hint = getHintTypeface(hint)
    }

    fun setAllCaps() {
        binding.txtInput.apply {
            inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        }
    }

    fun setSuffix(suffix: String) {
        if (suffix.isNotEmpty()) {
            binding.lblSuffix.text = suffix
            binding.lblSuffix.visibility = View.VISIBLE
        }
    }

    fun getSuffix(): String {
        return binding.lblSuffix.text.toString()
    }

    fun setInfo(info: String, infoSize: Float = 14f) {
        binding.lblAlert.text = info
        binding.lblAlert.setVisible(info.isNotEmpty())
        binding.lblAlert.setTextSize(TypedValue.COMPLEX_UNIT_SP, infoSize)
    }

    fun setInfoAppearance(style: Int) {
        binding.lblAlert.setTextAppearance(style)
    }

    fun setCounter(limit: String) {
        limitCounter = limit
        binding.lblAlert.setVisible(limit.isNotEmpty())
        binding.lblAlert.text =
            String.format(context.getStringRes(R.string.required), "0", limit)
        binding.lblAlert.gravity = Gravity.END
    }

    fun setVisibleAllert(view: Int) {
        binding.lblAlert.visibility = view
    }

    fun setMaxLength(length: Int) {
        binding.txtInput.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(length))
    }

    fun setInputTextStyle(color: Int) {
        binding.txtInput.setTextAppearance(color)
        binding.txtInput.setHintTextColor(ContextCompat.getColor(mContext, R.color.color_text_secondary))
    }

    fun setForMultiLine(){
        binding.constInput.layoutParams.width = LayoutParams.MATCH_PARENT
        binding.constInput.layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100F, resources.displayMetrics).toInt()
        binding.txtInput.layoutParams.height = LayoutParams.MATCH_PARENT
        binding.txtInput.setLines(3)
        binding.txtInput.setPadding(0,(11 * resources.displayMetrics.density + 0.5f).toInt(),0,0)
        binding.txtInput.gravity = Gravity.TOP
        binding.txtInput.gravity = Gravity.START
        binding.imgInputType.visibility = View.GONE
    }

    fun resetInput() {
        binding.lblInputValue.text = binding.txtInput.hint
        binding.lblInputValue.setTextColor(
            ContextCompat.getColor(
                mContext,
                R.color.color_text_secondary
            )
        )
        binding.txtInput.setText("")
    }

    private fun getHintTypeface(hint: String): SpannableString {
        val typeFontBook = ResourcesCompat.getFont(context, R.font.roboto) as Typeface
        val typefaceSpan = CustomTypefaceSpan(typeFontBook)
        val spannableString = SpannableString(hint)
        spannableString.setSpan(
            typefaceSpan,
            0,
            spannableString.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    fun setText(input: String) {
        binding.lblInputValue.text = input
        binding.lblInputValue.setTextColor(
            ContextCompat.getColor(
                mContext,
                R.color.color_secondary
            )
        )
        binding.txtInput.setText(input)
        //binding.txtInput.setSelection(input.length)
    }

    fun setInputValueStyle(color: Int) {
        binding.lblInputValue.setTextAppearance(color)
    }

    fun setInputType(type: TYPE) {
        inputType = type
        binding.txtInput.inputType = when (type) {
            TYPE.TEXT -> InputType.TYPE_CLASS_TEXT
            TYPE.PASSWORD -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            TYPE.PASSWORD_NUMBER -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            TYPE.NUMBER -> InputType.TYPE_CLASS_NUMBER
            TYPE.EMAIL -> InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            TYPE.DISABLE -> InputType.TYPE_CLASS_TEXT
            TYPE.CURRENCY -> InputType.TYPE_CLASS_NUMBER
            TYPE.PHONE_OR_EMAIL -> InputType.TYPE_CLASS_PHONE or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            TYPE.MENU -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE
            TYPE.FIELD_NO_SUGGESTION -> InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        binding.txtInput.setVisible(type != TYPE.DISABLE)

        binding.lblInputValue.setVisible(type == TYPE.DISABLE)
        binding.lblInputValue.setTextColor(
            ContextCompat.getColor(
                mContext,
                R.color.color_text_secondary
            )
        )

        if (inputType == TYPE.PASSWORD || inputType == TYPE.PASSWORD_NUMBER) {
            binding.cbPassword.buttonDrawable?.changeDrawableColor(
                ContextCompat.getColor(context, R.color.color_primary)
            )
            binding.cbPassword.setVisible(true)
        } else {
            binding.cbPassword.setVisible(false)
        }
    }

    /**Call this in initialization only
     */
    fun setClearTextEnable(isEnable: Boolean) {
        if (
            inputType != TYPE.PASSWORD || inputType != TYPE.PASSWORD_NUMBER
        ) {
            Log.d(TAG, "Input type: !password or !password number")
            binding.imgClearText.apply {
                setVisible(isEnable)
                setOnClickListener { clearText() }
            }
        } else {
            Log.d(TAG, "Input type: password or password number")
        }
        isClearTextEnable = isEnable
    }

    fun showClearText(isVisible: Boolean) {
        binding.imgClearText.setVisible(isVisible)
    }

    fun setImeOption(action: ACTION) {
        binding.txtInput.imeOptions = when (action) {
            ACTION.NEXT -> EditorInfo.IME_ACTION_NEXT
            ACTION.DONE -> EditorInfo.IME_ACTION_DONE
        }
    }

    fun setVisibleIcon(visible: Boolean, drawable: Int? = null, forceSetFill: Boolean = false) {
        binding.imgInputType.setVisible(visible)
        if (drawable != null) {
            val iconInput = ContextCompat.getDrawable(mContext, drawable) as Drawable
            binding.imgInputType.setImageDrawable(iconInput)
            binding.imgInputType.drawable.setFill(forceSetFill)
        }
    }

    fun setVisibleIconAction(
        visible: Boolean,
        drawable: Int? = null,
        forceSetFill: Boolean = false
    ) {
        binding.imgInputAction.setVisible(visible)
        if (drawable != null) {
            val iconInput = ContextCompat.getDrawable(mContext, drawable) as Drawable
            binding.imgInputAction.setImageDrawable(iconInput)
            binding.imgInputAction.drawable.setFill(forceSetFill)
        }
    }

    fun setVisibleIconFillAction(visible: Boolean, drawable: Int? = null) {
        binding.imgInputAction.setVisible(visible)
        if (drawable != null) {
            val iconInput = ContextCompat.getDrawable(mContext, drawable) as Drawable
            binding.imgInputAction.setImageDrawable(iconInput)
        }
    }

    fun clearText() {
        binding.txtInput.setText("")
        setFieldError(false)
        setClearTextEnable(false)
        binding.lblAlert.visibility = View.GONE
    }

//    fun setSuffixSpinner(listSpinner: ArrayList<String>) {
//        this.listSpinner = listSpinner
//        binding.spinnerSuffix.setScrollable(listSpinner.size)
//        binding.spinnerSuffix.adapter = context?.spinnerAdapter(listSpinner)
//        binding.spinnerSuffix.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                setSuffix(listSpinner[position])
//                listener?.onSuffixChanged()
//            }
//        }
//        binding.lblSuffix.setCompoundDrawablesWithIntrinsicBounds(
//            0,
//            0,
//            R.drawable.ic_chevron_down_white,
//            0
//        )
//    }

    fun setDeleteMode(isDelete: Boolean) {
        isDeleteMode = isDelete
    }

    fun setError(error: String? = null) {
        binding.txtInput.error = error
    }

    fun setListener(inputListener: InputListener) {
        listener = inputListener
    }

    fun getText(): String {
        return if (isMasking) plainText
        else binding.txtInput.text.toString()
    }

    fun getError(): String {
        return if (binding.txtInput.error == null) "" else binding.txtInput.error.toString()
    }

    fun setTextColor(color: Int) {
        binding.txtInput.setTextColor(ContextCompat.getColor(context, color))
    }

    fun setFieldError(isFieldError: Boolean) {
        when (isFieldError) {
            true -> {
                val colorRed = ContextCompat.getColor(context, R.color.color_red)
                val clearText = ContextCompat.getDrawable(context, androidx.appcompat.R.drawable.abc_ic_clear_material)
                binding.lblInput.setTextColor(colorRed)
                binding.txtInput.setTextColor(colorRed)
                binding.lblAlert.setTextColor(colorRed)
                binding.imgInputType.drawable.changeDrawableColor(colorRed)
                binding.cbPassword.buttonDrawable?.changeDrawableColor(colorRed)
                binding.constInput.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_textbox)
                binding.imgClearText.setImageDrawable(clearText)
            }
            false -> {
                val colorDarkHard = ContextCompat.getColor(context, R.color.color_text_primary)
                val colorDarkMedium = ContextCompat.getColor(context, R.color.color_secondary)
                val colorGrayMedium = ContextCompat.getColor(context, R.color.color_secondary)
                val colorBlueMedium = ContextCompat.getColor(context, R.color.color_text_secondary)
                val clearText = ContextCompat.getDrawable(context, androidx.appcompat.R.drawable.abc_ic_clear_material)
                binding.lblInput.setTextColor(colorDarkHard)
                binding.txtInput.setTextColor(colorDarkHard)
                binding.lblAlert.setTextColor(colorDarkMedium)
                binding.imgInputType.drawable.changeDrawableColor(colorGrayMedium)
                binding.cbPassword.buttonDrawable?.changeDrawableColor(colorBlueMedium)
                binding.constInput.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_textbox)
                binding.imgClearText.setImageDrawable(clearText)
            }
        }
        this.isFieldError = isFieldError
    }

    fun getIsFieldError(): Boolean {
        return isFieldError
    }

    fun setBackgroundBase(res: Int) {
        binding.constInput.setBackgroundResource(res)
    }

    fun setDisabledClick(isClick: Boolean) {
        binding.txtInput.isEnabled = isClick
        this.isClick = isClick
        when (isClick) {
            true -> binding.constInput.setBackgroundResource(R.drawable.bg_textbox)
            false -> binding.constInput.setBackgroundResource(R.drawable.bg_textbox)
        }
    }

    fun getDisableClick(): Boolean{
        return isClick
    }

    private fun Drawable.changeDrawableColor(color: Int) {
        when (this) {
            is ShapeDrawable -> this.paint.color = color // cast to 'ShapeDrawable'
            is GradientDrawable -> // cast to 'GradientDrawable'
                this.setColor(color)
            is ColorDrawable -> // alpha value may need to be set again after this call
                this.color = color
            else -> this.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    private fun Drawable.setFill(isNotEmpty: Boolean) {
        val color = ContextCompat.getColor(
            context,
            if (isNotEmpty) R.color.color_text_secondary else R.color.color_text_secondary
        )
        this.changeDrawableColor(color)
    }

    fun setAction(action: () -> Unit) {
        binding.imgInputAction.setOnClickListener {
            action.invoke()
        }
    }

    fun overrideClear(drawable: Int? = null) {
        binding.imgClearText.visibility = View.VISIBLE
        if (drawable != null) {
            val iconInput = ContextCompat.getDrawable(mContext, drawable) as Drawable
            binding.imgClearText.setImageDrawable(iconInput)
            binding.imgClearText.setOnClickListener { listener?.onInputClick() }
        }
    }

    private fun maskText(input: String): String {
        val maskingBuilder = StringBuilder()
        return when (maskingType) {
            MASKING_TYPE.LOGIN_FIELD -> {
                if (input.length > 4) {
                    for ((index, char) in input.withIndex()) {
                        if (index > 1 && index < input.length - 2)
                            maskingBuilder.append("*")
                        else maskingBuilder.append(char)
                    }
                    maskingBuilder.toString()
                } else {
                    input
                }
            }
            MASKING_TYPE.CGV -> {
                for (i in input.indices) {
                    maskingBuilder.append("*")
                }
                maskingBuilder.toString()
            }
            else -> input
        }
    }

    interface InputListener {
        fun afterTextChanged() {}
        fun onInputIconClick() {}
        fun onInputClick() {}
        fun onSuffixClick() {}
        fun onFilter(s: CharSequence?) {}
        fun onSuffixChanged() {}
    }
}