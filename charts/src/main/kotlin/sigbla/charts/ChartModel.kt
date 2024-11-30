/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.charts

import com.beust.klaxon.Render
import java.util.*

interface ChartModelElement {
    fun escapeString(s: String?): String = if (s == null) "null" else "\"" + Render.escapeString(s) + "\""
    fun escapeListString(s: List<String?>?): String = if (s == null) "null" else "[" + s.joinToString(separator = ",") { escapeString(it) } + "]"
    fun escapeListNumber(s: List<Number?>?): String = if (s == null) "null" else "[" + s.joinToString(separator = ",") { it.toString() } + "]"

    fun serializeKeyValue(buffer: StringBuffer, vararg items: Pair<String, ChartModelElement?>) {
        buffer.append("{")
        items.filter { it.second != null }.forEachIndexed { index, item ->
            if (index > 0) buffer.append(",")
            buffer.append(escapeString(item.first))
            buffer.append(": ")
            item.second!!.serialize(buffer)
        }
        buffer.append("}")
    }

    fun serialize(buffer: StringBuffer)
}

abstract class Bool :
    ChartModel.Data.Datasets.Dataset.BorderSkipped,
    ChartModel.Data.Datasets.Dataset.Fill,
    ChartModel.Data.Datasets.Dataset.Stepped,
    ChartModel.Data.Datasets.Dataset.SpanGaps,
    ChartModel.Options.SpanGaps,
    ChartModel.Options.Responsive,
    ChartModel.Options.Fill,
    ChartModel.Options.Interaction.Intersect,
    ChartModel.Options.Interaction.IncludeInvisible,
    ChartModel.Options.Stacked,
    ChartModel.Options.Scales.Scale.Time.IsoWeekday,
    ChartModel.Options.Scales.Scale.Stacked,
    ChartModel.Options.Scales.Scale.Display,
    ChartModel.Options.Scales.Scale.Title.Display,
    ChartModel.Options.Scales.Scale.Ticks.AutoSkip,
    ChartModel.Options.Scales.Scale.Ticks.Display,
    ChartModel.Options.Scales.Scale.Ticks.Major.Enabled,
    ChartModel.Options.Scales.Scale.Ticks.ShowLabelBackdrop,
    ChartModel.Options.Scales.Scale.Offset,
    ChartModel.Options.Scales.Scale.Reverse,
    ChartModel.Options.Scales.Scale.Border.Display,
    ChartModel.Options.Scales.Scale.Grid.Circular,
    ChartModel.Options.Scales.Scale.Grid.Display,
    ChartModel.Options.Scales.Scale.Grid.DrawOnChartArea,
    ChartModel.Options.Scales.Scale.Grid.DrawTicks,
    ChartModel.Options.Scales.Scale.Grid.Offset,
    ChartModel.Options.Scales.Scale.PointLabels.CenterPointLabels,
    ChartModel.Options.Scales.Scale.PointLabels.Display,
    ChartModel.Options.Plugins.Legend.Display,
    ChartModel.Options.Plugins.Legend.FullSize,
    ChartModel.Options.Plugins.Legend.Reverse,
    ChartModel.Options.Plugins.Legend.Labels.UsePointStyle,
    ChartModel.Options.Plugins.Legend.Labels.UseBorderRadius,
    ChartModel.Options.Plugins.Legend.RTL,
    ChartModel.Options.Plugins.Legend.Title.Display,
    ChartModel.Options.Plugins.Title.Display,
    ChartModel.Options.Plugins.Title.FullSize,
    ChartModel.Options.Plugins.Subtitle.Display,
    ChartModel.Options.Plugins.Subtitle.FullSize,
    ChartModel.Options.Plugins.Tooltip.Enabled,
    ChartModel.Options.Plugins.Tooltip.Intersect,
    ChartModel.Options.Plugins.Tooltip.DisplayColors,
    ChartModel.Options.Plugins.Tooltip.UsePointStyle,
    ChartModel.Options.Plugins.Tooltip.RTL,
    ChartModel.Options.Plugins.Filler.Propagate
{
    object True : Bool() {
        override fun serialize(buffer: StringBuffer) { buffer.append(true.toString()) }
    }

    object False :
        Bool(),
        ChartModel.Data.Datasets.Dataset.PointStyle,
        ChartModel.Options.Plugins.Legend.Labels.PointStyle
    {
        override fun serialize(buffer: StringBuffer) { buffer.append(false.toString()) }
    }
}

open class Text(
    var label: String
) : SingleType,
    ChartModel.Data.Datasets.Dataset.Label,
    ChartModel.Data.Datasets.Dataset.Stack,
    ChartModel.Data.Datasets.Dataset.Fill,
    ChartModel.Data.Datasets.Dataset.Stepped,
    ChartModel.Data.Datasets.Dataset.YAxisID,
    ChartModel.Options.IndexAxis,
    ChartModel.Options.Scales.Scale.Ticks.Source,
    ChartModel.Options.Scales.Scale.Time.DisplayFormats.Millisecond,
    ChartModel.Options.Scales.Scale.Time.DisplayFormats.Second,
    ChartModel.Options.Scales.Scale.Time.DisplayFormats.Minute,
    ChartModel.Options.Scales.Scale.Time.DisplayFormats.Hour,
    ChartModel.Options.Scales.Scale.Time.DisplayFormats.Day,
    ChartModel.Options.Scales.Scale.Time.DisplayFormats.Week,
    ChartModel.Options.Scales.Scale.Time.DisplayFormats.Month,
    ChartModel.Options.Scales.Scale.Time.DisplayFormats.Quarter,
    ChartModel.Options.Scales.Scale.Time.DisplayFormats.Year,
    ChartModel.Options.Scales.Scale.Time.Parser,
    ChartModel.Options.Scales.Scale.Time.Round,
    ChartModel.Options.Scales.Scale.Time.TooltipFormat,
    ChartModel.Options.Scales.Scale.Stacked,
    ChartModel.Options.Scales.Scale.Stack,
    ChartModel.Options.Scales.Scale.Title.Text,
    ChartModel.Options.Plugins.Legend.Title.Text,
    ChartModel.Options.Plugins.Title.Text,
    ChartModel.Options.Plugins.Subtitle.Text,
    Font.Family,
    Font.Style,
    Font.LineHeight
{
    override fun serialize(buffer: StringBuffer) { buffer.append(escapeString(label)) }
}

open class Numeric(
    var value: Number
) : SingleType,
    ChartModel.Data.Datasets.Dataset.BorderWidth,
    ChartModel.Data.Datasets.Dataset.BorderRadius,
    ChartModel.Data.Datasets.Dataset.Tension,
    ChartModel.Data.Datasets.Dataset.PointRadius,
    ChartModel.Data.Datasets.Dataset.PointHoverRadius,
    ChartModel.Data.Datasets.Dataset.SpanGaps,
    ChartModel.Data.Datasets.Dataset.Order,
    ChartModel.Options.SpanGaps,
    ChartModel.Options.Radius,
    ChartModel.Options.Scales.Scale.Time.IsoWeekday,
    ChartModel.Options.Scales.Scale.StackWeight,
    Font.Size,
    Font.Weight,
    Font.LineHeight,
    ChartModel.Options.Scales.Scale.Title.Padding,
    ChartModel.Options.Scales.Scale.Min,
    ChartModel.Options.Scales.Scale.Max,
    ChartModel.Options.Scales.Scale.SuggestedMin,
    ChartModel.Options.Scales.Scale.SuggestedMax,
    ChartModel.Options.Scales.Scale.Ticks.BackdropPadding,
    ChartModel.Options.Scales.Scale.Ticks.MaxRotation,
    ChartModel.Options.Scales.Scale.Ticks.Padding,
    ChartModel.Options.Scales.Scale.Ticks.StepSize,
    ChartModel.Options.Scales.Scale.Ticks.TextStrokeWidth,
    ChartModel.Options.Scales.Scale.Ticks.Z,
    ChartModel.Options.Scales.Scale.Border.Width,
    ChartModel.Options.Scales.Scale.Border.DashOffset,
    ChartModel.Options.Scales.Scale.Border.Z,
    ChartModel.Options.Scales.Scale.Grid.LineWidth,
    ChartModel.Options.Scales.Scale.Grid.TickBorderDash,
    ChartModel.Options.Scales.Scale.Grid.TickBorderDashOffset,
    ChartModel.Options.Scales.Scale.Grid.TickLength,
    ChartModel.Options.Scales.Scale.Grid.TickWidth,
    ChartModel.Options.Scales.Scale.Grid.Z,
    ChartModel.Options.Scales.Scale.PointLabels.BackdropPadding,
    ChartModel.Options.Scales.Scale.PointLabels.BorderRadius,
    ChartModel.Options.Scales.Scale.PointLabels.Padding,
    ChartModel.Options.Plugins.Legend.MaxHeight,
    ChartModel.Options.Plugins.Legend.MaxWidth,
    ChartModel.Options.Plugins.Legend.Labels.BoxWidth,
    ChartModel.Options.Plugins.Legend.Labels.BoxHeight,
    ChartModel.Options.Plugins.Legend.Labels.Padding,
    ChartModel.Options.Plugins.Legend.Labels.PointStyleWidth,
    ChartModel.Options.Plugins.Legend.Labels.BorderRadius,
    ChartModel.Options.Plugins.Legend.Title.Padding,
    ChartModel.Options.Plugins.Title.Padding,
    ChartModel.Options.Plugins.Subtitle.Padding,
    ChartModel.Options.Plugins.Tooltip.TitleSpacing,
    ChartModel.Options.Plugins.Tooltip.TitleMarginBottom,
    ChartModel.Options.Plugins.Tooltip.BodySpacing,
    ChartModel.Options.Plugins.Tooltip.FooterSpacing,
    ChartModel.Options.Plugins.Tooltip.FooterMarginTop,
    ChartModel.Options.Plugins.Tooltip.Padding,
    ChartModel.Options.Plugins.Tooltip.CaretPadding,
    ChartModel.Options.Plugins.Tooltip.CaretSize,
    ChartModel.Options.Plugins.Tooltip.CornerRadius,
    ChartModel.Options.Plugins.Tooltip.BoxWidth,
    ChartModel.Options.Plugins.Tooltip.BoxHeight,
    ChartModel.Options.Plugins.Tooltip.BoxPadding,
    ChartModel.Options.Plugins.Tooltip.BorderWidth,
    ChartModel.Options.Animation.Duration
{
    override fun serialize(buffer: StringBuffer) { buffer.append(value.toString()) }
}

interface SingleType : ChartModelElement

open class Strings(
    var values: List<String?>
) :
    SingleType,
    ChartModel.Data.Labels,
    ChartModel.Data.Datasets.Dataset.Data,
    ChartModel.Options.Scales.Scale.Labels,
    ChartModel.Options.Plugins.Title.Text,
    ChartModel.Options.Plugins.Subtitle.Text
{
    constructor(vararg values: String?) : this(values.toList())

    override fun serialize(buffer: StringBuffer) {
        buffer.append(escapeListString(values))
    }
}

open class Numbers(
    var values: List<Number?>
) : SingleType,
    ChartModel.Data.Datasets.Dataset.Data,
    ChartModel.Data.Datasets.Dataset.BorderDash,
    ChartModel.Options.Scales.Scale.Border.Dash
{
    constructor(vararg values: Number?) : this(values.toList())

    override fun serialize(buffer: StringBuffer) { buffer.append(escapeListNumber(values)) }
}

open class NumberPairs(
    var values: List<Pair<Number, Number>>
) : ChartModel.Data.Datasets.Dataset.Data {
    constructor(vararg values: Pair<Number, Number>) : this(values.toList())

    override fun serialize(buffer: StringBuffer) {
        buffer.append("[")
        values.forEachIndexed { index, pair ->
            if (index > 0) buffer.append(", ")
            buffer.append(escapeListNumber(pair.toList()))
        }
        buffer.append("]")
    }
}

open class Complex(
    var values: List<Map<String, SingleType>>
) : ChartModel.Data.Datasets.Dataset.Data {
    constructor(vararg values: Map<String, SingleType>) : this(values.toList())

    override fun serialize(buffer: StringBuffer) {
        buffer.append("[")

        values.forEachIndexed { index1, complexValues ->
            if (index1 > 0) buffer.append(",")
            buffer.append("{")

            complexValues.asSequence().forEachIndexed { index2, entry ->
                if (index2 > 0) buffer.append(",")

                val key = entry.key
                val value = entry.value
                buffer.append("\"$key\":")
                value.serialize(buffer)
            }

            buffer.append("}")
        }

        buffer.append("]")
    }
}

abstract class ChartType :
    ChartModel.Type,
    ChartModel.Data.Datasets.Dataset.Type
{
    object Bar : ChartType() {
        override fun serialize(buffer: StringBuffer) { buffer.append("\"bar\"") }
    }
    object Line : ChartType() {
        override fun serialize(buffer: StringBuffer) { buffer.append("\"line\"") }
    }
    object Bubble : ChartType() {
        override fun serialize(buffer: StringBuffer) { buffer.append("\"bubble\"") }
    }
    object Doughnut : ChartType() {
        override fun serialize(buffer: StringBuffer) { buffer.append("\"doughnut\"") }
    }
    object Pie : ChartType() {
        override fun serialize(buffer: StringBuffer) { buffer.append("\"pie\"") }
    }
    object PolarArea : ChartType() {
        override fun serialize(buffer: StringBuffer) { buffer.append("\"polarArea\"") }
    }
    object Radar : ChartType() {
        override fun serialize(buffer: StringBuffer) { buffer.append("\"radar\"") }
    }
    object Scatter : ChartType() {
        override fun serialize(buffer: StringBuffer) { buffer.append("\"scatter\"") }
    }
}

abstract class Color :
    ChartModel.Data.Datasets.Dataset.BorderColor,
    ChartModel.Data.Datasets.Dataset.BackgroundColor,
    ChartModel.Data.Datasets.Dataset.PointBorderColor,
    ChartModel.Options.Scales.Scale.Title.Color,
    ChartModel.Options.Scales.Scale.Ticks.BackdropColor,
    ChartModel.Options.Scales.Scale.Ticks.Color,
    ChartModel.Options.Scales.Scale.Ticks.TextStrokeColor,
    ChartModel.Options.Scales.Scale.Border.Color,
    ChartModel.Options.Scales.Scale.Grid.Color,
    ChartModel.Options.Scales.Scale.Grid.TickColor,
    ChartModel.Options.Scales.Scale.PointLabels.BackdropColor,
    ChartModel.Options.Scales.Scale.PointLabels.Color,
    ChartModel.Options.Plugins.CustomCanvasBackgroundColor.Color,
    ChartModel.Options.Plugins.Legend.Labels.Color,
    ChartModel.Options.Plugins.Legend.Title.Color,
    ChartModel.Options.Plugins.Title.Color,
    ChartModel.Options.Plugins.Subtitle.Color,
    ChartModel.Options.Plugins.Tooltip.BackgroundColor,
    ChartModel.Options.Plugins.Tooltip.TitleColor,
    ChartModel.Options.Plugins.Tooltip.BodyColor,
    ChartModel.Options.Plugins.Tooltip.FooterColor,
    ChartModel.Options.Plugins.Tooltip.MultiKeyBackground,
    ChartModel.Options.Plugins.Tooltip.BorderColor
{
    open class RGBA(
        var red: Int,
        var green: Int,
        var blue: Int,
        var alpha: Double
    ) : Color() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("rgba($red,$green,$blue,$alpha)")) }
    }

    open class RGB(
        var red: Int,
        var green: Int,
        var blue: Int
    ) : Color() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("rgb($red,$green,$blue)")) }
    }

    open class HSL(
        var h: Int,
        var s: Int,
        var l: Int
    ) : Color() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("hsl($h,$s%,$l%)")) }
    }

    open class Hex(
        var hex: String
    ) : Color() {
        private fun verifyValue(): String {
            var hex = this.hex
            hex = hex.lowercase(Locale.getDefault())
            if (hex.startsWith("#")) hex = hex.substring(1)
            if (hex.length != 3) throw IllegalArgumentException("Invalid hex value length: #$hex")
            if (!hex.matches("[0-9a-f]+".toRegex())) throw IllegalArgumentException("Invalid hex value: #$hex")

            return hex
        }

        override fun serialize(buffer: StringBuffer) {
            buffer.append(escapeString("#${verifyValue()}"))
        }
    }

    // TODO Add names
}

open class Colors(
    var colors: List<Color>
) : ChartModel.Data.Datasets.Dataset.BackgroundColor {
    constructor(vararg color: Color) : this(color.toList())

    override fun serialize(buffer: StringBuffer) {
        buffer.append("[")
        colors.forEachIndexed { index, color ->
            if (index > 0) buffer.append(",")
            color.serialize(buffer)
        }
        buffer.append("]")
    }
}

abstract class CubicInterpolationMode : ChartModel.Data.Datasets.Dataset.CubicInterpolationMode {
    object Default : CubicInterpolationMode() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("default")) }
    }

    object Monotone : CubicInterpolationMode() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("monotone")) }
    }
}

abstract class PointStyle :
    ChartModel.Data.Datasets.Dataset.PointStyle,
    ChartModel.Options.Plugins.Legend.Labels.PointStyle
{
    object Circle : PointStyle() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("circle")) }
    }
    object Cross : PointStyle() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("cross")) }
    }
    object CrossRot : PointStyle() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("crossRot")) }
    }
    object Dash : PointStyle() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("dash")) }
    }
    object Line : PointStyle() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("line")) }
    }
    object Rect : PointStyle() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("rect")) }
    }
    object RectRounded : PointStyle() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("rectRounded")) }
    }
    object RectRot : PointStyle() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("rectRot")) }
    }
    object Star : PointStyle() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("star")) }
    }
    object Triangle : PointStyle() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("triangle")) }
    }
}

abstract class InteractionMode :
    ChartModel.Options.Interaction.Mode,
    ChartModel.Options.Plugins.Tooltip.Mode
{
    object Point : InteractionMode() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("point"))}
    }
    object Nearest : InteractionMode() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("nearest"))}
    }
    object Index : InteractionMode() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("index"))}
    }
    object Dataset : InteractionMode() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("dataset")) }
    }
    object X : InteractionMode() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("x")) }
    }
    object Y : InteractionMode() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("y")) }
    }
}

abstract class InteractionAxis : ChartModel.Options.Interaction.Axis {
    object X : InteractionAxis() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("x")) }
    }
    object Y : InteractionAxis() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("y")) }
    }
    object XY : InteractionAxis() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("xy")) }
    }
    object R : InteractionAxis() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("r")) }
    }
}

abstract class ScaleType : ChartModel.Options.Scales.Scale.Type {
    object Time : ScaleType() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("time")) }
    }
    object Logarithmic : ScaleType() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("logarithmic")) }
    }
    object Linear : ScaleType() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("linear")) }
    }
    object Category : ScaleType() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("category")) }
    }
}

abstract class TimeUnit :
    ChartModel.Options.Scales.Scale.Time.TimeUnit,
    ChartModel.Options.Scales.Scale.Time.MinTimeUnit
{
    object Millisecond : TimeUnit() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("millisecond")) }
    }
    object Second : TimeUnit() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("second")) }
    }
    object Minute : TimeUnit() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("minute")) }
    }
    object Hour : TimeUnit() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("hour")) }
    }
    object Day : TimeUnit() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("day")) }
    }
    object Week : TimeUnit() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("week")) }
    }
    object Month : TimeUnit() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("month")) }
    }
    object Quarter : TimeUnit() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("quarter")) }
    }
    object Year : TimeUnit() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("year")) }
    }
}

abstract class Display :
    ChartModel.Options.Scales.Scale.Display,
    ChartModel.Options.Scales.Scale.PointLabels.Display
{
    object Auto : Display() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("auto")) }
    }
}

abstract class Align :
    ChartModel.Options.Scales.Scale.Title.Align,
    ChartModel.Options.Plugins.Legend.Align,
    ChartModel.Options.Plugins.Title.Align,
    ChartModel.Options.Plugins.Subtitle.Align,
    ChartModel.Options.Plugins.Legend.Title.Position
{
    object Start : Align() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("start")) }
    }
    object Center : Align() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("center")) }
    }
    object End : Align() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("end")) }
    }
}

open class Font(
    var family: Family? = null,
    var size: Size? = null,
    var style: Style? = null,
    var weight: Weight? = null,
    var lineHeight: LineHeight? = null
) :
    ChartModel.Options.Scales.Scale.Title.Font,
    ChartModel.Options.Scales.Scale.Ticks.Font,
    ChartModel.Options.Scales.Scale.PointLabels.Font,
    ChartModel.Options.Plugins.Legend.Labels.Font,
    ChartModel.Options.Plugins.Legend.Title.Font,
    ChartModel.Options.Plugins.Title.Font,
    ChartModel.Options.Plugins.Subtitle.Font,
    ChartModel.Options.Plugins.Tooltip.TitleFont,
    ChartModel.Options.Plugins.Tooltip.BodyFont,
    ChartModel.Options.Plugins.Tooltip.FooterFont
{
    interface Family : ChartModelElement
    interface Size : ChartModelElement
    interface Style : ChartModelElement
    interface Weight : ChartModelElement
    interface LineHeight : ChartModelElement

    override fun serialize(buffer: StringBuffer) {
        serializeKeyValue(buffer, "family" to family, "size" to size, "style" to style, "weight" to weight, "lineHeight" to lineHeight)
    }
}

abstract class FontWeight : Font.Weight {
    object Normal : FontWeight() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("normal")) }
    }
    object Bold : FontWeight() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("bold")) }
    }
    object Lighter : FontWeight() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("lighter")) }
    }
    object Bolder : FontWeight() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("bolder")) }
    }
}

abstract class Padding :
    ChartModel.Options.Scales.Scale.Title.Padding,
    ChartModel.Options.Scales.Scale.Ticks.BackdropPadding,
    ChartModel.Options.Scales.Scale.PointLabels.BackdropPadding,
    ChartModel.Options.Plugins.Legend.Title.Padding,
    ChartModel.Options.Plugins.Title.Padding,
    ChartModel.Options.Plugins.Subtitle.Padding,
    ChartModel.Options.Plugins.Tooltip.Padding
{
    open class TopLeftBottomRight(
        var top: Int = 0,
        var left: Int = 0,
        var bottom: Int = 0,
        var right: Int = 0
    ) : Padding() {
        override fun serialize(buffer: StringBuffer) {
            buffer.append("{")
            buffer.append("\"top\": $top,")
            buffer.append("\"left\": $left,")
            buffer.append("\"bottom\": $bottom,")
            buffer.append("\"right\": $right")
            buffer.append("}")
        }
    }

    open class XY(
        var x: Int,
        var y: Int
    ) : Padding() {
        override fun serialize(buffer: StringBuffer) {
            buffer.append("{")
            buffer.append("\"x\": $x,")
            buffer.append("\"y\": $y")
            buffer.append("}")
        }
    }
}

abstract class Position :
    ChartModel.Options.Scales.Scale.Position,
    ChartModel.Options.Plugins.Legend.Position,
    ChartModel.Options.Plugins.Title.Position,
    ChartModel.Options.Plugins.Subtitle.Position
{
    object Top : Position() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("top")) }
    }
    object Left : Position() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("left")) }
    }
    object Bottom : Position() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("bottom")) }
    }
    object Right : Position() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("right")) }
    }
}

abstract class HorizontalTextAlign :
    ChartModel.Options.Plugins.Legend.Labels.TextAlign,
    ChartModel.Options.Plugins.Tooltip.TitleAlign,
    ChartModel.Options.Plugins.Tooltip.BodyAlign,
    ChartModel.Options.Plugins.Tooltip.FooterAlign,
    ChartModel.Options.Plugins.Tooltip.XAlign
{
    object Left : HorizontalTextAlign() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("left")) }
    }
    object Right : HorizontalTextAlign() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("right")) }
    }
    object Center : HorizontalTextAlign() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("center")) }
    }
}

abstract class VerticalTextAlign : ChartModel.Options.Plugins.Tooltip.YAlign {
    object Top : VerticalTextAlign() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("top")) }
    }

    object Bottom : VerticalTextAlign() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("bottom")) }
    }

    object Center : VerticalTextAlign() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("center")) }
    }
}

abstract class TextDirection :
    ChartModel.Options.Plugins.Legend.TextDirection,
    ChartModel.Options.Plugins.Tooltip.TextDirection
{
    object LTR : Position() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("ltr")) }
    }
    object RTL : Position() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("rtl")) }
    }
}

abstract class TooltipPosition :
    ChartModel.Options.Plugins.Tooltip.Position
{
    object Average : TooltipPosition() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("average")) }
    }
    object Nearest : TooltipPosition() {
        override fun serialize(buffer: StringBuffer) { buffer.append(escapeString("nearest")) }
    }
}

open class ChartModel (
    var type: Type? = null,
    var data: Data? = null,
    var options: Options? = null,
    var parser: String? = null
) : ChartModelElement {
    interface Type : ChartModelElement

    open class Data(
        var labels: Labels? = null,
        var datasets: Datasets? = null
    ) : ChartModelElement {
        interface Labels : ChartModelElement

        open class Datasets(
            var datasets: List<Dataset>
        ) : ChartModelElement {
            constructor(vararg datasets: Dataset) : this(datasets.toList())

            open class Dataset(
                var label: Label? = null,
                var data: Data? = null,
                var borderColor: BorderColor? = null,
                var backgroundColor: BackgroundColor? = null,
                var borderDash: BorderDash? = null,
                var borderWidth: BorderWidth? = null,
                var borderRadius: BorderRadius? = null,
                var borderSkipped: BorderSkipped? = null,
                var stack: Stack? = null,
                var fill: Fill? = null,
                var stepped: Stepped? = null,
                var cubicInterpolationMode: CubicInterpolationMode? = null,
                var tension: Tension? = null,
                var yAxisID: YAxisID? = null,
                var pointStyle: PointStyle? = null,
                var pointRadius: PointRadius? = null,
                var pointHoverRadius: PointHoverRadius? = null,
                var pointBorderColor: PointBorderColor? = null,
                // var segment..
                var spanGaps: SpanGaps? = null,
                var order: Order? = null,
                var type: Type? = null
            ) : ChartModelElement {
                interface Label : ChartModelElement
                interface Data : ChartModelElement
                interface BorderColor : ChartModelElement
                interface BackgroundColor : ChartModelElement
                interface BorderDash : ChartModelElement
                interface BorderWidth : ChartModelElement
                interface BorderRadius : ChartModelElement
                interface BorderSkipped : ChartModelElement
                interface Stack : ChartModelElement
                interface Fill : ChartModelElement
                interface Stepped : ChartModelElement
                interface CubicInterpolationMode : ChartModelElement
                interface Tension : ChartModelElement
                interface YAxisID : ChartModelElement
                interface PointStyle : ChartModelElement
                interface PointRadius : ChartModelElement
                interface PointHoverRadius : ChartModelElement
                interface PointBorderColor : ChartModelElement
                interface SpanGaps : ChartModelElement
                interface Order : ChartModelElement
                interface Type : ChartModelElement

                override fun serialize(buffer: StringBuffer) {
                    serializeKeyValue(
                        buffer,
                        "label" to label,
                        "data" to data,
                        "borderColor" to borderColor,
                        "backgroundColor" to backgroundColor,
                        "borderDash" to borderDash,
                        "borderWidth" to borderWidth,
                        "borderRadius" to borderRadius,
                        "borderSkipped" to borderSkipped,
                        "stack" to stack,
                        "fill" to fill,
                        "stepped" to stepped,
                        "cubicInterpolationMode" to cubicInterpolationMode,
                        "tension" to tension,
                        "yAxisID" to yAxisID,
                        "pointStyle" to pointStyle,
                        "pointRadius" to pointRadius,
                        "pointHoverRadius" to pointHoverRadius,
                        "pointBorderColor" to pointBorderColor,
                        "spanGaps" to spanGaps,
                        "order" to order,
                        "type" to type
                    )
                }
            }

            override fun serialize(buffer: StringBuffer) {
                buffer.append("[")
                datasets.forEachIndexed { index, dataset ->
                    if (index > 0) buffer.append(",")
                    dataset.serialize(buffer)
                }
                buffer.append("]")
            }
        }

        override fun serialize(buffer: StringBuffer) {
            serializeKeyValue(buffer, "labels" to labels, "datasets" to datasets)
        }
    }

    open class Options(
        var spanGaps: SpanGaps? = null,
        var indexAxis: IndexAxis? = null,
        // TODO var elements: Elements? = null,
        var responsive: Responsive? = null,
        var fill: Fill? = null,
        var radius: Radius? = null,
        var interaction: Interaction? = null,
        var stacked: Stacked? = null,
        var scales: Scales? = null,
        var plugins: Plugins? = null,
        var animation: Animation? = null
    ) : ChartModelElement {
        interface SpanGaps : ChartModelElement
        interface IndexAxis : ChartModelElement
        interface Responsive : ChartModelElement
        interface Fill : ChartModelElement
        interface Radius : ChartModelElement

        open class Interaction(
            var mode: Mode? = null,
            var intersect: Intersect? = null,
            var axis: Axis? = null,
            var includeInvisible: IncludeInvisible? = null
        ) : ChartModelElement {
            interface Mode : ChartModelElement
            interface Intersect : ChartModelElement
            interface Axis : ChartModelElement
            interface IncludeInvisible : ChartModelElement

            override fun serialize(buffer: StringBuffer) {
                serializeKeyValue(
                    buffer,
                    "mode" to mode,
                    "intersect" to intersect,
                    "axis" to axis,
                    "includeInvisible" to includeInvisible
                )
            }
        }

        interface Stacked : ChartModelElement

        open class Scales(
            var values: Map<String, Scale>? = null
        ) : ChartModelElement {
            constructor(vararg values: Pair<String, Scale>) : this(values.toMap())

            open class Scale(
                var type: Type? = null,
                var time: Time? = null,
                var stacked: Stacked? = null,
                var stack: Stack? = null,
                var stackWeight: StackWeight? = null,
                var display: Display? = null,
                var title: Title? = null,
                var min: Min? = null,
                var max: Max? = null,
                var suggestedMin: SuggestedMin? = null,
                var suggestedMax: SuggestedMax? = null,
                var ticks: Ticks? = null,
                var labels: Labels? = null,
                var offset: Offset? = null,
                var reverse: Reverse? = null,
                var position: Position? = null,
                var border: Border? = null,
                var grid: Grid? = null,
                var pointLabels: PointLabels? = null
            ) : ChartModelElement {
                interface Type : ChartModelElement

                open class Time(
                    var displayFormats: DisplayFormats? = null,
                    var isoWeekday: IsoWeekday? = null,
                    var parser: Parser? = null,
                    var round: Round? = null,
                    var tooltipFormat: TooltipFormat? = null,
                    var unit: TimeUnit? = null,
                    var minUnit: MinTimeUnit? = null
                ) : ChartModelElement {
                    open class DisplayFormats(
                        var millisecond: Millisecond? = null,
                        var second: Second? = null,
                        var minute: Minute? = null,
                        var hour: Hour? = null,
                        var day: Day? = null,
                        var week: Week? = null,
                        var month: Month? = null,
                        var quarter: Quarter? = null,
                        var year: Year? = null
                    ) : ChartModelElement {
                        interface Millisecond : ChartModelElement
                        interface Second : ChartModelElement
                        interface Minute : ChartModelElement
                        interface Hour : ChartModelElement
                        interface Day : ChartModelElement
                        interface Week : ChartModelElement
                        interface Month : ChartModelElement
                        interface Quarter : ChartModelElement
                        interface Year : ChartModelElement

                        override fun serialize(buffer: StringBuffer) {
                            serializeKeyValue(
                                buffer,
                                "millisecond" to millisecond,
                                "second" to second,
                                "minute" to minute,
                                "hour" to hour,
                                "day" to day,
                                "week" to week,
                                "month" to month,
                                "quarter" to quarter,
                                "year" to year
                            )
                        }
                    }

                    interface IsoWeekday : ChartModelElement
                    interface Parser : ChartModelElement
                    interface Round : ChartModelElement
                    interface TooltipFormat : ChartModelElement
                    interface TimeUnit : ChartModelElement
                    interface MinTimeUnit : ChartModelElement

                    override fun serialize(buffer: StringBuffer) {
                        serializeKeyValue(
                            buffer,
                            "displayFormats" to displayFormats,
                            "isoWeekday" to isoWeekday,
                            "parser" to parser,
                            "round" to round,
                            "tooltipFormat" to tooltipFormat,
                            "unit" to unit,
                            "minUnit" to minUnit
                        )
                    }
                }

                interface Stacked : ChartModelElement
                interface Stack : ChartModelElement
                interface StackWeight : ChartModelElement
                interface Display : ChartModelElement

                open class Title(
                    var display: Display? = null,
                    var align: Align? = null,
                    var text: Text? = null,
                    var color: Color? = null,
                    var font: Font? = null,
                    var padding: Padding? = null
                ) : ChartModelElement {
                    interface Display : ChartModelElement
                    interface Align : ChartModelElement
                    interface Text : ChartModelElement
                    interface Color : ChartModelElement
                    interface Font : ChartModelElement
                    interface Padding : ChartModelElement

                    override fun serialize(buffer: StringBuffer) {
                        serializeKeyValue(
                            buffer,
                            "display" to display,
                            "align" to align,
                            "text" to text,
                            "color" to color,
                            "font" to font,
                            "padding" to padding
                        )
                    }
                }

                interface Min : ChartModelElement
                interface Max : ChartModelElement
                interface SuggestedMin : ChartModelElement
                interface SuggestedMax : ChartModelElement

                open class Ticks(
                    var autoSkip: AutoSkip? = null,
                    var backdropColor: BackdropColor? = null,
                    var backdropPadding: BackdropPadding? = null,
                    // var callback..
                    var display: Display? = null,
                    var color: Color? = null,
                    var font: Font? = null,
                    var major: Major? = null,
                    var maxRotation: MaxRotation? = null,
                    var padding: Padding? = null,
                    var showLabelBackdrop: ShowLabelBackdrop? = null,
                    var source: Source? = null,
                    var stepSize: StepSize? = null,
                    var textStrokeColor: TextStrokeColor? = null,
                    var textStrokeWidth: TextStrokeWidth? = null,
                    var z: Z? = null
                ) : ChartModelElement {
                    interface AutoSkip : ChartModelElement
                    interface BackdropColor : ChartModelElement
                    interface BackdropPadding : ChartModelElement
                    interface Display : ChartModelElement
                    interface Color : ChartModelElement
                    interface Font : ChartModelElement
                    interface MaxRotation : ChartModelElement

                    open class Major(
                        var enabled: Enabled
                    ) : ChartModelElement {
                        interface Enabled : ChartModelElement

                        override fun serialize(buffer: StringBuffer) {
                            serializeKeyValue(buffer, "enabled" to enabled)
                        }
                    }

                    interface Padding : ChartModelElement
                    interface ShowLabelBackdrop : ChartModelElement
                    interface Source: ChartModelElement
                    interface StepSize : ChartModelElement
                    interface TextStrokeColor : ChartModelElement
                    interface TextStrokeWidth : ChartModelElement
                    interface Z : ChartModelElement

                    override fun serialize(buffer: StringBuffer) {
                        serializeKeyValue(
                            buffer,
                            "backdropColor" to backdropColor,
                            "backdropPadding" to backdropPadding,
                            "display" to display,
                            "color" to color,
                            "font" to font,
                            "major" to major,
                            "padding" to padding,
                            "showLabelBackdrop" to showLabelBackdrop,
                            "stepSize" to stepSize,
                            "textStrokeColor" to textStrokeColor,
                            "textStrokeWidth" to textStrokeWidth,
                            "z" to z
                        )
                    }
                }

                interface Labels : ChartModelElement
                interface Offset : ChartModelElement
                interface Reverse : ChartModelElement
                interface Position : ChartModelElement

                open class Border(
                    var display: Display? = null,
                    var color: Color? = null,
                    var width: Width? = null,
                    var dash: Dash? = null,
                    var dashOffset: DashOffset? = null,
                    var z: Z? = null,
                ) : ChartModelElement {
                    interface Display : ChartModelElement
                    interface Color : ChartModelElement
                    interface Width : ChartModelElement
                    interface Dash : ChartModelElement
                    interface DashOffset : ChartModelElement
                    interface Z : ChartModelElement

                    override fun serialize(buffer: StringBuffer) {
                        serializeKeyValue(
                            buffer,
                            "display" to display,
                            "color" to color,
                            "width" to width,
                            "dash" to dash,
                            "dashOffset" to dashOffset,
                            "z" to z
                        )
                    }
                }

                open class Grid(
                    var circular: Circular? = null,
                    var color: Color? = null,
                    var display: Display? = null,
                    var drawOnChartArea: DrawOnChartArea? = null,
                    var drawTicks: DrawTicks? = null,
                    var lineWidth: LineWidth? = null,
                    var offset: Offset? = null,
                    var tickBorderDash: TickBorderDash? = null,
                    var tickBorderDashOffset: TickBorderDashOffset? = null,
                    var tickColor: TickColor? = null,
                    var tickWidth: TickWidth? = null,
                    var z: Z? = null
                ) : ChartModelElement {
                    interface Circular : ChartModelElement
                    interface Color : ChartModelElement
                    interface Display : ChartModelElement
                    interface DrawOnChartArea : ChartModelElement
                    interface DrawTicks : ChartModelElement
                    interface LineWidth : ChartModelElement
                    interface Offset : ChartModelElement
                    interface TickBorderDash : ChartModelElement
                    interface TickBorderDashOffset : ChartModelElement
                    interface TickColor : ChartModelElement
                    interface TickLength : ChartModelElement
                    interface TickWidth : ChartModelElement
                    interface Z : ChartModelElement

                    override fun serialize(buffer: StringBuffer) {
                        serializeKeyValue(
                            buffer,
                            "circular" to circular,
                            "color" to color,
                            "display" to display,
                            "drawOnChartArea" to drawOnChartArea,
                            "drawTicks" to drawTicks,
                            "lineWidth" to lineWidth,
                            "offset" to offset,
                            "tickBorderDash" to tickBorderDash,
                            "tickBorderDashOffset" to tickBorderDashOffset,
                            "tickColor" to tickColor,
                            "tickWidth" to tickWidth,
                            "z" to z
                        )
                    }
                }

                open class PointLabels(
                    var backdropColor: BackdropColor? = null,
                    var backdropPadding: BackdropPadding? = null,
                    var borderRadius: BorderRadius? = null,
                    var display: Display? = null,
                    // var callback..
                    var color: Color? = null,
                    var font: Font? = null,
                    var padding: Padding? = null,
                    var centerPointLabels: CenterPointLabels? = null
                ) : ChartModelElement {
                    interface BackdropColor : ChartModelElement
                    interface BackdropPadding : ChartModelElement
                    interface BorderRadius : ChartModelElement
                    interface Display : ChartModelElement
                    interface Color : ChartModelElement
                    interface Font : ChartModelElement
                    interface Padding : ChartModelElement
                    interface CenterPointLabels : ChartModelElement

                    override fun serialize(buffer: StringBuffer) {
                        serializeKeyValue(
                            buffer,
                            "backdropColor" to backdropColor,
                            "backdropPadding" to backdropPadding,
                            "borderRadius" to borderRadius,
                            "display" to display,
                            "color" to color,
                            "font" to font,
                            "padding" to padding,
                            "centerPointLabels" to centerPointLabels
                        )
                    }
                }

                override fun serialize(buffer: StringBuffer) {
                    serializeKeyValue(
                        buffer,
                        "type" to type,
                        "time" to time,
                        "stacked" to stacked,
                        "stack" to stack,
                        "stackWeight" to stackWeight,
                        "display" to display,
                        "title" to title,
                        "min" to min,
                        "max" to max,
                        "suggestedMin" to suggestedMin,
                        "suggestedMax" to suggestedMax,
                        "ticks" to ticks,
                        "labels" to labels,
                        "offset" to offset,
                        "reverse" to reverse,
                        "position" to position,
                        "border" to border,
                        "grid" to grid,
                        "pointLabels" to pointLabels
                    )
                }
            }

            override fun serialize(buffer: StringBuffer) {
                val v = values
                if (v != null) {
                    serializeKeyValue(
                        buffer,
                        *v.map { it.key to it.value }.toTypedArray()
                    )
                }
            }
        }

        open class Plugins(
            var customCanvasBackgroundColor: CustomCanvasBackgroundColor? = null,
            var legend: Legend? = null,
            var title: Title? = null,
            var subtitle: Subtitle? = null,
            var tooltip: Tooltip? = null,
            var filler: Filler? = null,
        ) : ChartModelElement {
            open class CustomCanvasBackgroundColor(
                var color: Color? = null,
            ) : ChartModelElement {
                interface Color : ChartModelElement

                override fun serialize(buffer: StringBuffer) {
                    serializeKeyValue(
                        buffer,
                        "color" to color
                    )
                }
            }

            open class Legend(
                var display: Display? = null,
                var position: Position? = null,
                var align: Align? = null,
                var maxHeight: MaxHeight? = null,
                var maxWidth: MaxWidth? = null,
                var fullSize: FullSize? = null,
                // var onClick, onHover, onLeave,
                var reverse: Reverse? = null,
                var labels: Labels? = null,
                var rtl: RTL? = null,
                var textDirection: TextDirection? = null,
                var title: Title? = null
            ) : ChartModelElement {
                interface Display : ChartModelElement
                interface Position : ChartModelElement
                interface Align : ChartModelElement
                interface MaxHeight : ChartModelElement
                interface MaxWidth : ChartModelElement
                interface FullSize : ChartModelElement
                interface Reverse : ChartModelElement

                open class Labels(
                    var boxWidth: BoxWidth? = null,
                    var boxHeight: BoxHeight? = null,
                    var color: Color? = null,
                    var font: Font? = null,
                    var padding: Padding? = null,
                    // var generateLabels, filter, sort
                    var pointStyle: PointStyle? = null,
                    var textAlign: TextAlign? = null,
                    var usePointStyle: UsePointStyle? = null,
                    var pointStyleWidth: PointStyleWidth? = null,
                    var useBorderRadius: UseBorderRadius? = null,
                    var borderRadius: BorderRadius? = null
                ) : ChartModelElement {
                    interface BoxWidth : ChartModelElement
                    interface BoxHeight : ChartModelElement
                    interface Color : ChartModelElement
                    interface Font : ChartModelElement
                    interface Padding : ChartModelElement
                    interface PointStyle : ChartModelElement
                    interface TextAlign : ChartModelElement
                    interface UsePointStyle : ChartModelElement
                    interface PointStyleWidth : ChartModelElement
                    interface UseBorderRadius : ChartModelElement
                    interface BorderRadius : ChartModelElement

                    override fun serialize(buffer: StringBuffer) {
                        serializeKeyValue(
                            buffer,
                            "boxWidth" to boxWidth,
                            "boxHeight" to boxHeight,
                            "color" to color,
                            "font" to font,
                            "padding" to padding,
                            "pointStyle" to pointStyle,
                            "textAlign" to textAlign,
                            "usePointStyle" to usePointStyle,
                            "pointStyleWidth" to pointStyleWidth,
                            "useBorderRadius" to useBorderRadius,
                            "borderRadius" to borderRadius
                        )
                    }
                }

                interface RTL : ChartModelElement
                interface TextDirection : ChartModelElement

                open class Title(
                    var color: Color? = null,
                    var display: Display? = null,
                    var font: Font? = null,
                    var padding: Padding? = null,
                    var position: Position? = null,
                    var text: Text? = null
                ) : ChartModelElement {
                    interface Color : ChartModelElement
                    interface Display : ChartModelElement
                    interface Font: ChartModelElement
                    interface Padding : ChartModelElement
                    interface Position: ChartModelElement
                    interface Text : ChartModelElement

                    override fun serialize(buffer: StringBuffer) {
                        serializeKeyValue(
                            buffer,
                            "color" to color,
                            "display" to display,
                            "font" to font,
                            "padding" to padding,
                            "text" to text
                        )
                    }
                }

                override fun serialize(buffer: StringBuffer) {
                    serializeKeyValue(
                        buffer,
                        "display" to display,
                        "position" to position,
                        "align" to align,
                        "maxHeight" to maxHeight,
                        "maxWidth" to maxWidth,
                        "fullSize" to fullSize,
                        "reverse" to reverse,
                        "labels" to labels,
                        "rtl" to rtl,
                        "textDirection" to textDirection,
                        "title" to title
                    )
                }
            }

            open class Title(
                var align: Align? = null,
                var color: Color? = null,
                var display: Display? = null,
                var fullSize: FullSize? = null,
                var position: Position? = null,
                var font: Font? = null,
                var padding: Padding? = null,
                var text: Text? = null
            ) : ChartModelElement {
                interface Align : ChartModelElement
                interface Color : ChartModelElement
                interface Display : ChartModelElement
                interface FullSize : ChartModelElement
                interface Position : ChartModelElement
                interface Font: ChartModelElement
                interface Padding : ChartModelElement
                interface Text : ChartModelElement

                override fun serialize(buffer: StringBuffer) {
                    serializeKeyValue(
                        buffer,
                        "align" to align,
                        "color" to color,
                        "display" to display,
                        "fullSize" to fullSize,
                        "position" to position,
                        "font" to font,
                        "padding" to padding,
                        "text" to text
                    )
                }
            }

            open class Subtitle(
                var align: Align? = null,
                var color: Color? = null,
                var display: Display? = null,
                var fullSize: FullSize? = null,
                var position: Position? = null,
                var font: Font? = null,
                var padding: Padding? = null,
                var text: Text? = null
            ) : ChartModelElement {
                interface Align : ChartModelElement
                interface Color : ChartModelElement
                interface Display : ChartModelElement
                interface FullSize : ChartModelElement
                interface Position : ChartModelElement
                interface Font: ChartModelElement
                interface Padding : ChartModelElement
                interface Text : ChartModelElement

                override fun serialize(buffer: StringBuffer) {
                    serializeKeyValue(
                        buffer,
                        "align" to align,
                        "color" to color,
                        "display" to display,
                        "fullSize" to fullSize,
                        "position" to position,
                        "font" to font,
                        "padding" to padding,
                        "text" to text
                    )
                }
            }

            open class Tooltip(
                var enabled: Enabled? = null,
                // var external
                var mode: Mode? = null,
                var intersect: Intersect? = null,
                var position: Position? = null,
                // var callbacks
                // var itemSort
                // var filter
                var backgroundColor: BackgroundColor? = null,
                var titleColor: TitleColor? = null,
                var titleFont: TitleFont? = null,
                var titleAlign: TitleAlign? = null,
                var titleSpacing: TitleSpacing? = null,
                var titleMarginBottom: TitleMarginBottom? = null,
                var bodyColor: BodyColor? = null,
                var bodyFont: BodyFont? = null,
                var bodyAlign: BodyAlign? = null,
                var bodySpacing: BodySpacing? = null,
                var footerColor: FooterColor? = null,
                var footerFont: FooterFont? = null,
                var footerAlign: FooterAlign? = null,
                var footerSpacing: FooterSpacing? = null,
                var footerMarginTop: FooterMarginTop? = null,
                var padding: Padding? = null,
                var caretPadding: CaretPadding? = null,
                var caretSize: CaretSize? = null,
                var cornerRadius: CornerRadius? = null,
                var multiKeyBackground: MultiKeyBackground? = null,
                var displayColors: DisplayColors? = null,
                var boxWidth: BoxWidth? = null,
                var boxHeight: BoxHeight? = null,
                var boxPadding: BoxPadding? = null,
                var usePointStyle: UsePointStyle? = null,
                var borderColor: BorderColor? = null,
                var borderWidth: BorderWidth? = null,
                var rtl: RTL? = null,
                var textDirection: TextDirection? = null,
                var xAlign: XAlign? = null,
                var yAlign: YAlign? = null
            ) : ChartModelElement {
                interface Enabled : ChartModelElement
                interface Mode : ChartModelElement
                interface Intersect : ChartModelElement
                interface Position : ChartModelElement
                interface BackgroundColor : ChartModelElement
                interface TitleColor : ChartModelElement
                interface TitleFont : ChartModelElement
                interface TitleAlign : ChartModelElement
                interface TitleSpacing : ChartModelElement
                interface TitleMarginBottom : ChartModelElement
                interface BodyColor : ChartModelElement
                interface BodyFont : ChartModelElement
                interface BodyAlign : ChartModelElement
                interface BodySpacing : ChartModelElement
                interface FooterColor : ChartModelElement
                interface FooterFont : ChartModelElement
                interface FooterAlign : ChartModelElement
                interface FooterSpacing : ChartModelElement
                interface FooterMarginTop : ChartModelElement
                interface Padding : ChartModelElement
                interface CaretPadding : ChartModelElement
                interface CaretSize : ChartModelElement
                interface CornerRadius : ChartModelElement
                interface MultiKeyBackground : ChartModelElement
                interface DisplayColors : ChartModelElement
                interface BoxWidth : ChartModelElement
                interface BoxHeight : ChartModelElement
                interface BoxPadding : ChartModelElement
                interface UsePointStyle : ChartModelElement
                interface BorderColor : ChartModelElement
                interface BorderWidth : ChartModelElement
                interface RTL : ChartModelElement
                interface TextDirection : ChartModelElement
                interface XAlign : ChartModelElement
                interface YAlign : ChartModelElement

                override fun serialize(buffer: StringBuffer) {
                    serializeKeyValue(
                        buffer,
                        "enabled" to enabled,
                        "mode" to mode,
                        "intersect" to intersect,
                        "position" to position,
                        "backgroundColor" to backgroundColor,
                        "titleColor" to titleColor,
                        "titleFont" to titleFont,
                        "titleAlign" to titleAlign,
                        "titleSpacing" to titleSpacing,
                        "titleMarginBottom" to titleMarginBottom,
                        "bodyColor" to bodyColor,
                        "bodyFont" to bodyFont,
                        "bodyAlign" to bodyAlign,
                        "bodySpacing" to bodySpacing,
                        "footerColor" to footerColor,
                        "footerFont" to footerFont,
                        "footerAlign" to footerAlign,
                        "footerSpacing" to footerSpacing,
                        "footerMarginTop" to footerMarginTop,
                        "padding" to padding,
                        "caretPadding" to caretPadding,
                        "caretSize" to caretSize,
                        "cornerRadius" to cornerRadius,
                        "multiKeyBackground" to multiKeyBackground,
                        "displayColors" to displayColors,
                        "boxWidth" to boxWidth,
                        "boxHeight" to boxHeight,
                        "boxPadding" to boxPadding,
                        "usePointStyle" to usePointStyle,
                        "borderColor" to borderColor,
                        "borderWidth" to borderWidth,
                        "rtl" to rtl,
                        "textDirection" to textDirection,
                        "xAlign" to xAlign,
                        "yAlign" to yAlign
                    )
                }
            }

            open class Filler(
                var propagate: Propagate? = null
            ) : ChartModelElement {
                interface Propagate : ChartModelElement

                override fun serialize(buffer: StringBuffer) {
                    serializeKeyValue(
                        buffer,
                        "propagate" to propagate
                    )
                }
            }

            override fun serialize(buffer: StringBuffer) {
                serializeKeyValue(
                    buffer,
                    "customCanvasBackgroundColor" to customCanvasBackgroundColor,
                    "legend" to legend,
                    "title" to title,
                    "subtitle" to subtitle,
                    "tooltip" to tooltip,
                    "filler" to filler
                )
            }
        }

        open class Animation(
            var duration: Duration? = null
        ) : ChartModelElement {
            interface Duration : ChartModelElement

            override fun serialize(buffer: StringBuffer) {
                serializeKeyValue(buffer, "duration" to duration)
            }
        }

        override fun serialize(buffer: StringBuffer) {
            serializeKeyValue(
                buffer,
                "spanGaps" to spanGaps,
                "indexAxis" to indexAxis,
                "responsive" to responsive,
                "fill" to fill,
                "radius" to radius,
                "interaction" to interaction,
                "stacked" to stacked,
                "scales" to scales,
                "plugins" to plugins,
                "animation" to animation
            )
        }
    }

    override fun serialize(buffer: StringBuffer) {
        val parser = this.parser
        if (parser != null)
            serializeKeyValue(buffer, "type" to type, "data" to data, "options" to options, "parser" to Text(parser))
        else
            serializeKeyValue(buffer, "type" to type, "data" to data, "options" to options)
    }
}
