package com.construction_worker_forum_back.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* com.construction_worker_forum_back.service..getAll*())")
    public void getAllValuesMethodsPointcut() {
    }

    @Pointcut("execution(* com.construction_worker_forum_back.service..findBy*(..))")
    public void findValueByIdMethodsPointcut() {
    }

    @Pointcut("execution(* com.construction_worker_forum_back.service..delete*(..))")
    public void deleteValueMethodsPointcut() {
    }

    @Pointcut("execution(* com.construction_worker_forum_back.service..create*(..))")
    public void createValueMethodsPointcut() {
    }

    @Pointcut("execution(* com.construction_worker_forum_back.service..update*(..))")
    public void updateValueMethodsPointcut() {
    }
}
