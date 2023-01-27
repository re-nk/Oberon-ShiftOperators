package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.ast.{Constant => ASTConstant, _}
import br.unb.cic.oberon.ir.tac._
import br.unb.cic.oberon.tc.{ExpressionTypeVisitor, TypeChecker}

object TACodeGenerator extends CodeGenerator[List[TAC]] {
  
  val visitor = new ExpressionTypeVisitor(new TypeChecker())

  override def generateCode(module: OberonModule): List[TAC] = {
    List()
  }

  def generateProcedure() {}

  def generateBody() {}

  def generateExpression(expr: Expression, insts: List[TAC]): (Address, List[TAC]) = {
    expr match {//usar visitExpression do tc para ver as expressoes

      case Brackets(exp) =>
        return generateExpression(exp, insts)

      case IntValue(value) =>
        return (Constant(value.toString, IntegerType), insts)

      case RealValue(value) =>
        return (Constant(value.toString, RealType), insts)

      case CharValue(value) =>
        return (Constant(value.toString, CharacterType), insts)

      case BoolValue(value) =>
        return (Constant(value.toString, BooleanType), insts)

      case StringValue(value) =>
        return (Constant(value, StringType), insts)

      case NullValue =>
        return (Constant("Null", NullType), insts)

      //TODO PROCURAR TIPO DA VARIAVEL
      //case VarExpression(name) =>
      //  return (Name(name, tipo), insts)

      case AddExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t = new Temporary(expr.accept(visitor).get)
        return (t, insts2 :+ AddOp(l, r, t, ""))

      case SubExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t = new Temporary(expr.accept(visitor).get)
        return (t, insts2 :+ SubOp(l, r, t, ""))

      case MultExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t = new Temporary(expr.accept(visitor).get)
        return (t, insts2 :+ MulOp(l, r, t, ""))

      case DivExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t = new Temporary(expr.accept(visitor).get)
        return (t, insts2 :+ DivOp(l, r, t, ""))
        
      case AndExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t = new Temporary(expr.accept(visitor).get)
        return (t, insts2 :+ AndOp(l, r, t, ""))

      case OrExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t = new Temporary(expr.accept(visitor).get)
        return (t, insts2 :+ OrOp(l, r, t, ""))

      case ModExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t = new Temporary(IntegerType)
        return (t, insts2 :+ RemOp(l, r, t, ""))

      case NotExpression(exp) =>
        val (a, insts1) = generateExpression(exp, insts)
        val t = new Temporary(BooleanType)
        return (t, insts1 :+ NotOp(a, t, ""))

      case EQExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t0 = new Temporary(expr.accept(visitor).get)
        val t1 = new Temporary(expr.accept(visitor).get)
        return (t1, insts2 :+ SubOp(l, r, t0, "") :+ SLTUOp(t0, Constant("1", IntegerType), t1, ""))

      case NEQExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t0 = new Temporary(expr.accept(visitor).get)
        val t1 = new Temporary(expr.accept(visitor).get)
        return (t1, insts2 :+ SubOp(l, r, t0, "") :+ SLTUOp(Constant("0", IntegerType), t0, t1, ""))

      case GTExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t = new Temporary(expr.accept(visitor).get)
        return (t, insts2 :+ SLTOp(r, l, t, ""))

      case LTExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t = new Temporary(expr.accept(visitor).get)
        return (t, insts2 :+ SLTOp(l, r, t, ""))

      case GTEExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t0 = new Temporary(expr.accept(visitor).get)
        val t1 = new Temporary(expr.accept(visitor).get)
        return (t1, insts2 :+ SLTOp(l, r, t0, "") :+ NotOp(t0, t1, ""))

      case LTEExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts1)
        val t0 = new Temporary(expr.accept(visitor).get)
        val t1 = new Temporary(expr.accept(visitor).get)
        return (t1, insts2 :+ SLTOp(r, l, t0, "") :+ NotOp(t0, t1, ""))

//TODO generateProcedure e gerar o Map funcs
//      case FunctionCallExpression(name, args) =>
//        val (args, argInsts) = exp.args.foldLeft((List[Address](),insts)) {
//          (acc, expr) => 
//            val (address, ops) = TACodeGenerator.generateExpression(expr, acc._2)
//            (acc._1 :+ address, ops)
//        }
//        val params = args.map(x => Param(x, ""))
//        //talvez mudar o funcs.get(name) para stack
//        return (funcs.get(name), argInsts ++ params :+ Call(name, args.length))

//TODO
//      case ArraySubscript(a, i) =>
//        return ()
//
//      case FieldAccessExpression(exp, name) =>
//        return ()
//
//      case PointerAccessExpression(name) =>
//        return ()
    }
  }

  def generateStatement() {}
}
