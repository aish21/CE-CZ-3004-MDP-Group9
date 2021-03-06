/* USER CODE BEGIN Header */
/**
  ******************************************************************************
  * @file           : main.c
  * @brief          : Main program body
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; Copyright (c) 2021 STMicroelectronics.
  * All rights reserved.</center></h2>
  *
  * This software component is licensed by ST under BSD 3-Clause license,
  * the "License"; You may not use this file except in compliance with the
  * License. You may obtain a copy of the License at:
  *                        opensource.org/licenses/BSD-3-Clause
  *
  ******************************************************************************
  */
/* USER CODE END Header */
/* Includes ------------------------------------------------------------------*/
#include "main.h"
#include "cmsis_os.h"

/* Private includes ----------------------------------------------------------*/
/* USER CODE BEGIN Includes */
#include "oled.h"
#include <stdbool.h>
#include <math.h>

/* USER CODE END Includes */

/* Private typedef -----------------------------------------------------------*/
/* USER CODE BEGIN PTD */

/* USER CODE END PTD */

/* Private define ------------------------------------------------------------*/
/* USER CODE BEGIN PD */
/* USER CODE END PD */

/* Private macro -------------------------------------------------------------*/
/* USER CODE BEGIN PM */

/* USER CODE END PM */

/* Private variables ---------------------------------------------------------*/
ADC_HandleTypeDef hadc1;
ADC_HandleTypeDef hadc2;

TIM_HandleTypeDef htim1;
TIM_HandleTypeDef htim2;
TIM_HandleTypeDef htim3;
TIM_HandleTypeDef htim4;
TIM_HandleTypeDef htim8;

UART_HandleTypeDef huart3;

/* Definitions for defaultTask */
osThreadId_t defaultTaskHandle;
const osThreadAttr_t defaultTask_attributes = {
  .name = "defaultTask",
  .stack_size = 128 * 4,
  .priority = (osPriority_t) osPriorityNormal,
};
/* Definitions for MotorTask */
osThreadId_t MotorTaskHandle;
const osThreadAttr_t MotorTask_attributes = {
  .name = "MotorTask",
  .stack_size = 512 * 4,
  .priority = (osPriority_t) osPriorityLow,
};
/* Definitions for showTask */
osThreadId_t showTaskHandle;
const osThreadAttr_t showTask_attributes = {
  .name = "showTask",
  .stack_size = 128 * 4,
  .priority = (osPriority_t) osPriorityLow,
};
/* Definitions for EncoderTask */
osThreadId_t EncoderTaskHandle;
const osThreadAttr_t EncoderTask_attributes = {
  .name = "EncoderTask",
  .stack_size = 128 * 4,
  .priority = (osPriority_t) osPriorityLow,
};
/* USER CODE BEGIN PV */

/* USER CODE END PV */

/* Private function prototypes -----------------------------------------------*/
void SystemClock_Config(void);
static void MX_GPIO_Init(void);
static void MX_TIM8_Init(void);
static void MX_TIM1_Init(void);
static void MX_TIM2_Init(void);
static void MX_USART3_UART_Init(void);
static void MX_TIM3_Init(void);
static void MX_ADC1_Init(void);
static void MX_ADC2_Init(void);
static void MX_TIM4_Init(void);
void StartDefaultTask(void *argument);
void motors(void *argument);
void show(void *argument);
void encoder_task(void *argument);

/* USER CODE BEGIN PFP */

/* USER CODE END PFP */

/* Private user code ---------------------------------------------------------*/
/* USER CODE BEGIN 0 */
//gv
uint8_t aRxBuffer[50];
uint16_t control;
uint16_t IR1_Val;
uint16_t IR2_Val;
uint8_t uSensor[20];
uint16_t test123 = 0;
uint32_t IC_Val1 = 0;
uint32_t IC_Val2 = 0;
uint32_t Difference = 0;
uint8_t Is_First_Captured = 0;
uint8_t uDistance = 0;

int total=0;
int addOsDelay;
float cal;
int checkdis = 0;

int left  = 57;
int right = 92;
int center = 75;
/* USER CODE END 0 */

/**
  * @brief  The application entry point.
  * @retval int
  */
int main(void)
{
  /* USER CODE BEGIN 1 */

  /* USER CODE END 1 */

  /* MCU Configuration--------------------------------------------------------*/

  /* Reset of all peripherals, Initializes the Flash interface and the Systick. */
  HAL_Init();

  /* USER CODE BEGIN Init */

  /* USER CODE END Init */

  /* Configure the system clock */
  SystemClock_Config();

  /* USER CODE BEGIN SysInit */

  /* USER CODE END SysInit */

  /* Initialize all configured peripherals */
  MX_GPIO_Init();
  MX_TIM8_Init();
  MX_TIM1_Init();
  MX_TIM2_Init();
  MX_USART3_UART_Init();
  MX_TIM3_Init();
  MX_ADC1_Init();
  MX_ADC2_Init();
  MX_TIM4_Init();
  /* USER CODE BEGIN 2 */
  OLED_Init();

  HAL_UART_Receive_IT(&huart3,(uint8_t *) aRxBuffer,50);
  /* USER CODE END 2 */

  /* Init scheduler */
  osKernelInitialize();

  /* USER CODE BEGIN RTOS_MUTEX */
  /* add mutexes, ... */
  /* USER CODE END RTOS_MUTEX */

  /* USER CODE BEGIN RTOS_SEMAPHORES */
  /* add semaphores, ... */
  /* USER CODE END RTOS_SEMAPHORES */

  /* USER CODE BEGIN RTOS_TIMERS */
  /* start timers, add new ones, ... */
  /* USER CODE END RTOS_TIMERS */

  /* USER CODE BEGIN RTOS_QUEUES */
  /* add queues, ... */
  /* USER CODE END RTOS_QUEUES */

  /* Create the thread(s) */
  /* creation of defaultTask */
  defaultTaskHandle = osThreadNew(StartDefaultTask, NULL, &defaultTask_attributes);

  /* creation of MotorTask */
  MotorTaskHandle = osThreadNew(motors, NULL, &MotorTask_attributes);

  /* creation of showTask */
  showTaskHandle = osThreadNew(show, NULL, &showTask_attributes);

  /* creation of EncoderTask */
  EncoderTaskHandle = osThreadNew(encoder_task, NULL, &EncoderTask_attributes);

  /* USER CODE BEGIN RTOS_THREADS */
  /* add threads, ... */
  /* USER CODE END RTOS_THREADS */

  /* USER CODE BEGIN RTOS_EVENTS */
  /* add events, ... */
  /* USER CODE END RTOS_EVENTS */

  /* Start scheduler */
  osKernelStart();

  /* We should never get here as control is now taken by the scheduler */
  /* Infinite loop */
  /* USER CODE BEGIN WHILE */
  while (1)
  {
    /* USER CODE END WHILE */

    /* USER CODE BEGIN 3 */

  }
  /* USER CODE END 3 */
}

/**
  * @brief System Clock Configuration
  * @retval None
  */
void SystemClock_Config(void)
{
  RCC_OscInitTypeDef RCC_OscInitStruct = {0};
  RCC_ClkInitTypeDef RCC_ClkInitStruct = {0};
  RCC_PeriphCLKInitTypeDef PeriphClkInit = {0};

  /** Initializes the RCC Oscillators according to the specified parameters
  * in the RCC_OscInitTypeDef structure.
  */
  RCC_OscInitStruct.OscillatorType = RCC_OSCILLATORTYPE_HSI;
  RCC_OscInitStruct.HSIState = RCC_HSI_ON;
  RCC_OscInitStruct.HSICalibrationValue = RCC_HSICALIBRATION_DEFAULT;
  RCC_OscInitStruct.PLL.PLLState = RCC_PLL_NONE;
  if (HAL_RCC_OscConfig(&RCC_OscInitStruct) != HAL_OK)
  {
    Error_Handler();
  }
  /** Initializes the CPU, AHB and APB buses clocks
  */
  RCC_ClkInitStruct.ClockType = RCC_CLOCKTYPE_HCLK|RCC_CLOCKTYPE_SYSCLK
                              |RCC_CLOCKTYPE_PCLK1|RCC_CLOCKTYPE_PCLK2;
  RCC_ClkInitStruct.SYSCLKSource = RCC_SYSCLKSOURCE_HSI;
  RCC_ClkInitStruct.AHBCLKDivider = RCC_SYSCLK_DIV1;
  RCC_ClkInitStruct.APB1CLKDivider = RCC_HCLK_DIV1;
  RCC_ClkInitStruct.APB2CLKDivider = RCC_HCLK_DIV1;

  if (HAL_RCC_ClockConfig(&RCC_ClkInitStruct, FLASH_LATENCY_0) != HAL_OK)
  {
    Error_Handler();
  }
  PeriphClkInit.PeriphClockSelection = RCC_PERIPHCLK_ADC;
  PeriphClkInit.AdcClockSelection = RCC_ADCPCLK2_DIV2;
  if (HAL_RCCEx_PeriphCLKConfig(&PeriphClkInit) != HAL_OK)
  {
    Error_Handler();
  }
}

/**
  * @brief ADC1 Initialization Function
  * @param None
  * @retval None
  */
static void MX_ADC1_Init(void)
{

  /* USER CODE BEGIN ADC1_Init 0 */

  /* USER CODE END ADC1_Init 0 */

  ADC_ChannelConfTypeDef sConfig = {0};

  /* USER CODE BEGIN ADC1_Init 1 */

  /* USER CODE END ADC1_Init 1 */
  /** Common config
  */
  hadc1.Instance = ADC1;
  hadc1.Init.ScanConvMode = ADC_SCAN_DISABLE;
  hadc1.Init.ContinuousConvMode = DISABLE;
  hadc1.Init.DiscontinuousConvMode = DISABLE;
  hadc1.Init.ExternalTrigConv = ADC_SOFTWARE_START;
  hadc1.Init.DataAlign = ADC_DATAALIGN_RIGHT;
  hadc1.Init.NbrOfConversion = 1;
  if (HAL_ADC_Init(&hadc1) != HAL_OK)
  {
    Error_Handler();
  }
  /** Configure Regular Channel
  */
  sConfig.Channel = ADC_CHANNEL_11;
  sConfig.Rank = ADC_REGULAR_RANK_1;
  sConfig.SamplingTime = ADC_SAMPLETIME_1CYCLE_5;
  if (HAL_ADC_ConfigChannel(&hadc1, &sConfig) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN ADC1_Init 2 */

  /* USER CODE END ADC1_Init 2 */

}

/**
  * @brief ADC2 Initialization Function
  * @param None
  * @retval None
  */
static void MX_ADC2_Init(void)
{

  /* USER CODE BEGIN ADC2_Init 0 */

  /* USER CODE END ADC2_Init 0 */

  ADC_ChannelConfTypeDef sConfig = {0};

  /* USER CODE BEGIN ADC2_Init 1 */

  /* USER CODE END ADC2_Init 1 */
  /** Common config
  */
  hadc2.Instance = ADC2;
  hadc2.Init.ScanConvMode = ADC_SCAN_DISABLE;
  hadc2.Init.ContinuousConvMode = DISABLE;
  hadc2.Init.DiscontinuousConvMode = DISABLE;
  hadc2.Init.ExternalTrigConv = ADC_SOFTWARE_START;
  hadc2.Init.DataAlign = ADC_DATAALIGN_RIGHT;
  hadc2.Init.NbrOfConversion = 1;
  if (HAL_ADC_Init(&hadc2) != HAL_OK)
  {
    Error_Handler();
  }
  /** Configure Regular Channel
  */
  sConfig.Channel = ADC_CHANNEL_12;
  sConfig.Rank = ADC_REGULAR_RANK_1;
  sConfig.SamplingTime = ADC_SAMPLETIME_1CYCLE_5;
  if (HAL_ADC_ConfigChannel(&hadc2, &sConfig) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN ADC2_Init 2 */

  /* USER CODE END ADC2_Init 2 */

}

/**
  * @brief TIM1 Initialization Function
  * @param None
  * @retval None
  */
static void MX_TIM1_Init(void)
{

  /* USER CODE BEGIN TIM1_Init 0 */

  /* USER CODE END TIM1_Init 0 */

  TIM_ClockConfigTypeDef sClockSourceConfig = {0};
  TIM_MasterConfigTypeDef sMasterConfig = {0};
  TIM_OC_InitTypeDef sConfigOC = {0};
  TIM_BreakDeadTimeConfigTypeDef sBreakDeadTimeConfig = {0};

  /* USER CODE BEGIN TIM1_Init 1 */

  /* USER CODE END TIM1_Init 1 */
  htim1.Instance = TIM1;
  htim1.Init.Prescaler = 160;
  htim1.Init.CounterMode = TIM_COUNTERMODE_UP;
  htim1.Init.Period = 1000;
  htim1.Init.ClockDivision = TIM_CLOCKDIVISION_DIV1;
  htim1.Init.RepetitionCounter = 0;
  htim1.Init.AutoReloadPreload = TIM_AUTORELOAD_PRELOAD_ENABLE;
  if (HAL_TIM_Base_Init(&htim1) != HAL_OK)
  {
    Error_Handler();
  }
  sClockSourceConfig.ClockSource = TIM_CLOCKSOURCE_INTERNAL;
  if (HAL_TIM_ConfigClockSource(&htim1, &sClockSourceConfig) != HAL_OK)
  {
    Error_Handler();
  }
  if (HAL_TIM_PWM_Init(&htim1) != HAL_OK)
  {
    Error_Handler();
  }
  sMasterConfig.MasterOutputTrigger = TIM_TRGO_RESET;
  sMasterConfig.MasterSlaveMode = TIM_MASTERSLAVEMODE_DISABLE;
  if (HAL_TIMEx_MasterConfigSynchronization(&htim1, &sMasterConfig) != HAL_OK)
  {
    Error_Handler();
  }
  sConfigOC.OCMode = TIM_OCMODE_PWM1;
  sConfigOC.Pulse = 0;
  sConfigOC.OCPolarity = TIM_OCPOLARITY_HIGH;
  sConfigOC.OCFastMode = TIM_OCFAST_DISABLE;
  sConfigOC.OCIdleState = TIM_OCIDLESTATE_RESET;
  sConfigOC.OCNIdleState = TIM_OCNIDLESTATE_RESET;
  if (HAL_TIM_PWM_ConfigChannel(&htim1, &sConfigOC, TIM_CHANNEL_4) != HAL_OK)
  {
    Error_Handler();
  }
  sBreakDeadTimeConfig.OffStateRunMode = TIM_OSSR_DISABLE;
  sBreakDeadTimeConfig.OffStateIDLEMode = TIM_OSSI_DISABLE;
  sBreakDeadTimeConfig.LockLevel = TIM_LOCKLEVEL_OFF;
  sBreakDeadTimeConfig.DeadTime = 0;
  sBreakDeadTimeConfig.BreakState = TIM_BREAK_DISABLE;
  sBreakDeadTimeConfig.BreakPolarity = TIM_BREAKPOLARITY_HIGH;
  sBreakDeadTimeConfig.AutomaticOutput = TIM_AUTOMATICOUTPUT_DISABLE;
  if (HAL_TIMEx_ConfigBreakDeadTime(&htim1, &sBreakDeadTimeConfig) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN TIM1_Init 2 */

  /* USER CODE END TIM1_Init 2 */
  HAL_TIM_MspPostInit(&htim1);

}

/**
  * @brief TIM2 Initialization Function
  * @param None
  * @retval None
  */
static void MX_TIM2_Init(void)
{

  /* USER CODE BEGIN TIM2_Init 0 */

  /* USER CODE END TIM2_Init 0 */

  TIM_Encoder_InitTypeDef sConfig = {0};
  TIM_MasterConfigTypeDef sMasterConfig = {0};

  /* USER CODE BEGIN TIM2_Init 1 */

  /* USER CODE END TIM2_Init 1 */
  htim2.Instance = TIM2;
  htim2.Init.Prescaler = 0;
  htim2.Init.CounterMode = TIM_COUNTERMODE_UP;
  htim2.Init.Period = 65535;
  htim2.Init.ClockDivision = TIM_CLOCKDIVISION_DIV1;
  htim2.Init.AutoReloadPreload = TIM_AUTORELOAD_PRELOAD_DISABLE;
  sConfig.EncoderMode = TIM_ENCODERMODE_TI12;
  sConfig.IC1Polarity = TIM_ICPOLARITY_RISING;
  sConfig.IC1Selection = TIM_ICSELECTION_DIRECTTI;
  sConfig.IC1Prescaler = TIM_ICPSC_DIV1;
  sConfig.IC1Filter = 10;
  sConfig.IC2Polarity = TIM_ICPOLARITY_RISING;
  sConfig.IC2Selection = TIM_ICSELECTION_DIRECTTI;
  sConfig.IC2Prescaler = TIM_ICPSC_DIV1;
  sConfig.IC2Filter = 0;
  if (HAL_TIM_Encoder_Init(&htim2, &sConfig) != HAL_OK)
  {
    Error_Handler();
  }
  sMasterConfig.MasterOutputTrigger = TIM_TRGO_RESET;
  sMasterConfig.MasterSlaveMode = TIM_MASTERSLAVEMODE_DISABLE;
  if (HAL_TIMEx_MasterConfigSynchronization(&htim2, &sMasterConfig) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN TIM2_Init 2 */

  /* USER CODE END TIM2_Init 2 */

}

/**
  * @brief TIM3 Initialization Function
  * @param None
  * @retval None
  */
static void MX_TIM3_Init(void)
{

  /* USER CODE BEGIN TIM3_Init 0 */

  /* USER CODE END TIM3_Init 0 */

  TIM_Encoder_InitTypeDef sConfig = {0};
  TIM_MasterConfigTypeDef sMasterConfig = {0};

  /* USER CODE BEGIN TIM3_Init 1 */

  /* USER CODE END TIM3_Init 1 */
  htim3.Instance = TIM3;
  htim3.Init.Prescaler = 0;
  htim3.Init.CounterMode = TIM_COUNTERMODE_UP;
  htim3.Init.Period = 65535;
  htim3.Init.ClockDivision = TIM_CLOCKDIVISION_DIV1;
  htim3.Init.AutoReloadPreload = TIM_AUTORELOAD_PRELOAD_DISABLE;
  sConfig.EncoderMode = TIM_ENCODERMODE_TI12;
  sConfig.IC1Polarity = TIM_ICPOLARITY_RISING;
  sConfig.IC1Selection = TIM_ICSELECTION_DIRECTTI;
  sConfig.IC1Prescaler = TIM_ICPSC_DIV1;
  sConfig.IC1Filter = 10;
  sConfig.IC2Polarity = TIM_ICPOLARITY_RISING;
  sConfig.IC2Selection = TIM_ICSELECTION_DIRECTTI;
  sConfig.IC2Prescaler = TIM_ICPSC_DIV1;
  sConfig.IC2Filter = 10;
  if (HAL_TIM_Encoder_Init(&htim3, &sConfig) != HAL_OK)
  {
    Error_Handler();
  }
  sMasterConfig.MasterOutputTrigger = TIM_TRGO_RESET;
  sMasterConfig.MasterSlaveMode = TIM_MASTERSLAVEMODE_DISABLE;
  if (HAL_TIMEx_MasterConfigSynchronization(&htim3, &sMasterConfig) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN TIM3_Init 2 */

  /* USER CODE END TIM3_Init 2 */

}

/**
  * @brief TIM4 Initialization Function
  * @param None
  * @retval None
  */
static void MX_TIM4_Init(void)
{

  /* USER CODE BEGIN TIM4_Init 0 */

  /* USER CODE END TIM4_Init 0 */

  TIM_MasterConfigTypeDef sMasterConfig = {0};
  TIM_IC_InitTypeDef sConfigIC = {0};

  /* USER CODE BEGIN TIM4_Init 1 */

  /* USER CODE END TIM4_Init 1 */
  htim4.Instance = TIM4;
  htim4.Init.Prescaler = 8-1;
  htim4.Init.CounterMode = TIM_COUNTERMODE_UP;
  htim4.Init.Period = 65535;
  htim4.Init.ClockDivision = TIM_CLOCKDIVISION_DIV1;
  htim4.Init.AutoReloadPreload = TIM_AUTORELOAD_PRELOAD_DISABLE;
  if (HAL_TIM_IC_Init(&htim4) != HAL_OK)
  {
    Error_Handler();
  }
  sMasterConfig.MasterOutputTrigger = TIM_TRGO_RESET;
  sMasterConfig.MasterSlaveMode = TIM_MASTERSLAVEMODE_DISABLE;
  if (HAL_TIMEx_MasterConfigSynchronization(&htim4, &sMasterConfig) != HAL_OK)
  {
    Error_Handler();
  }
  sConfigIC.ICPolarity = TIM_INPUTCHANNELPOLARITY_RISING;
  sConfigIC.ICSelection = TIM_ICSELECTION_DIRECTTI;
  sConfigIC.ICPrescaler = TIM_ICPSC_DIV1;
  sConfigIC.ICFilter = 0;
  if (HAL_TIM_IC_ConfigChannel(&htim4, &sConfigIC, TIM_CHANNEL_1) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN TIM4_Init 2 */

  /* USER CODE END TIM4_Init 2 */

}

/**
  * @brief TIM8 Initialization Function
  * @param None
  * @retval None
  */
static void MX_TIM8_Init(void)
{

  /* USER CODE BEGIN TIM8_Init 0 */

  /* USER CODE END TIM8_Init 0 */

  TIM_ClockConfigTypeDef sClockSourceConfig = {0};
  TIM_MasterConfigTypeDef sMasterConfig = {0};
  TIM_OC_InitTypeDef sConfigOC = {0};
  TIM_BreakDeadTimeConfigTypeDef sBreakDeadTimeConfig = {0};

  /* USER CODE BEGIN TIM8_Init 1 */

  /* USER CODE END TIM8_Init 1 */
  htim8.Instance = TIM8;
  htim8.Init.Prescaler = 0;
  htim8.Init.CounterMode = TIM_COUNTERMODE_UP;
  htim8.Init.Period = 7199;
  htim8.Init.ClockDivision = TIM_CLOCKDIVISION_DIV1;
  htim8.Init.RepetitionCounter = 0;
  htim8.Init.AutoReloadPreload = TIM_AUTORELOAD_PRELOAD_DISABLE;
  if (HAL_TIM_Base_Init(&htim8) != HAL_OK)
  {
    Error_Handler();
  }
  sClockSourceConfig.ClockSource = TIM_CLOCKSOURCE_INTERNAL;
  if (HAL_TIM_ConfigClockSource(&htim8, &sClockSourceConfig) != HAL_OK)
  {
    Error_Handler();
  }
  if (HAL_TIM_PWM_Init(&htim8) != HAL_OK)
  {
    Error_Handler();
  }
  sMasterConfig.MasterOutputTrigger = TIM_TRGO_RESET;
  sMasterConfig.MasterSlaveMode = TIM_MASTERSLAVEMODE_DISABLE;
  if (HAL_TIMEx_MasterConfigSynchronization(&htim8, &sMasterConfig) != HAL_OK)
  {
    Error_Handler();
  }
  sConfigOC.OCMode = TIM_OCMODE_PWM1;
  sConfigOC.Pulse = 0;
  sConfigOC.OCPolarity = TIM_OCPOLARITY_HIGH;
  sConfigOC.OCNPolarity = TIM_OCNPOLARITY_HIGH;
  sConfigOC.OCFastMode = TIM_OCFAST_DISABLE;
  sConfigOC.OCIdleState = TIM_OCIDLESTATE_RESET;
  sConfigOC.OCNIdleState = TIM_OCNIDLESTATE_RESET;
  if (HAL_TIM_PWM_ConfigChannel(&htim8, &sConfigOC, TIM_CHANNEL_1) != HAL_OK)
  {
    Error_Handler();
  }
  if (HAL_TIM_PWM_ConfigChannel(&htim8, &sConfigOC, TIM_CHANNEL_2) != HAL_OK)
  {
    Error_Handler();
  }
  sBreakDeadTimeConfig.OffStateRunMode = TIM_OSSR_DISABLE;
  sBreakDeadTimeConfig.OffStateIDLEMode = TIM_OSSI_DISABLE;
  sBreakDeadTimeConfig.LockLevel = TIM_LOCKLEVEL_OFF;
  sBreakDeadTimeConfig.DeadTime = 0;
  sBreakDeadTimeConfig.BreakState = TIM_BREAK_DISABLE;
  sBreakDeadTimeConfig.BreakPolarity = TIM_BREAKPOLARITY_HIGH;
  sBreakDeadTimeConfig.AutomaticOutput = TIM_AUTOMATICOUTPUT_DISABLE;
  if (HAL_TIMEx_ConfigBreakDeadTime(&htim8, &sBreakDeadTimeConfig) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN TIM8_Init 2 */

  /* USER CODE END TIM8_Init 2 */

}

/**
  * @brief USART3 Initialization Function
  * @param None
  * @retval None
  */
static void MX_USART3_UART_Init(void)
{

  /* USER CODE BEGIN USART3_Init 0 */

  /* USER CODE END USART3_Init 0 */

  /* USER CODE BEGIN USART3_Init 1 */

  /* USER CODE END USART3_Init 1 */
  huart3.Instance = USART3;
  huart3.Init.BaudRate = 115200;
  huart3.Init.WordLength = UART_WORDLENGTH_8B;
  huart3.Init.StopBits = UART_STOPBITS_1;
  huart3.Init.Parity = UART_PARITY_NONE;
  huart3.Init.Mode = UART_MODE_TX_RX;
  huart3.Init.HwFlowCtl = UART_HWCONTROL_NONE;
  huart3.Init.OverSampling = UART_OVERSAMPLING_16;
  if (HAL_UART_Init(&huart3) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN USART3_Init 2 */

  /* USER CODE END USART3_Init 2 */

}

/**
  * @brief GPIO Initialization Function
  * @param None
  * @retval None
  */
static void MX_GPIO_Init(void)
{
  GPIO_InitTypeDef GPIO_InitStruct = {0};

  /* GPIO Ports Clock Enable */
  __HAL_RCC_GPIOE_CLK_ENABLE();
  __HAL_RCC_GPIOC_CLK_ENABLE();
  __HAL_RCC_GPIOA_CLK_ENABLE();
  __HAL_RCC_GPIOD_CLK_ENABLE();
  __HAL_RCC_GPIOB_CLK_ENABLE();

  /*Configure GPIO pin Output Level */
  HAL_GPIO_WritePin(GPIOE, OLED_SCL_Pin|OLED_SDA_Pin|OLED_RST_Pin|OLED_DC_Pin
                          |LED_3_Pin, GPIO_PIN_RESET);

  /*Configure GPIO pin Output Level */
  HAL_GPIO_WritePin(GPIOA, AIN2_Pin|AIN1_Pin|BIN1_Pin|BIN2_Pin, GPIO_PIN_RESET);

  /*Configure GPIO pin Output Level */
  HAL_GPIO_WritePin(GPIOD, GPIO_PIN_13, GPIO_PIN_RESET);

  /*Configure GPIO pins : OLED_SCL_Pin OLED_SDA_Pin OLED_RST_Pin OLED_DC_Pin
                           LED_3_Pin */
  GPIO_InitStruct.Pin = OLED_SCL_Pin|OLED_SDA_Pin|OLED_RST_Pin|OLED_DC_Pin
                          |LED_3_Pin;
  GPIO_InitStruct.Mode = GPIO_MODE_OUTPUT_PP;
  GPIO_InitStruct.Pull = GPIO_NOPULL;
  GPIO_InitStruct.Speed = GPIO_SPEED_FREQ_LOW;
  HAL_GPIO_Init(GPIOE, &GPIO_InitStruct);

  /*Configure GPIO pins : AIN2_Pin AIN1_Pin BIN1_Pin BIN2_Pin */
  GPIO_InitStruct.Pin = AIN2_Pin|AIN1_Pin|BIN1_Pin|BIN2_Pin;
  GPIO_InitStruct.Mode = GPIO_MODE_OUTPUT_PP;
  GPIO_InitStruct.Pull = GPIO_NOPULL;
  GPIO_InitStruct.Speed = GPIO_SPEED_FREQ_HIGH;
  HAL_GPIO_Init(GPIOA, &GPIO_InitStruct);

  /*Configure GPIO pin : PD13 */
  GPIO_InitStruct.Pin = GPIO_PIN_13;
  GPIO_InitStruct.Mode = GPIO_MODE_OUTPUT_PP;
  GPIO_InitStruct.Pull = GPIO_NOPULL;
  GPIO_InitStruct.Speed = GPIO_SPEED_FREQ_LOW;
  HAL_GPIO_Init(GPIOD, &GPIO_InitStruct);

}

/* USER CODE BEGIN 4 */
void HAL_UART_RxCpltCallBack(UART_HandleTypeDef *huart)
{
	/* Prevent unused argument(s) compilation warning */
	UNUSED(huart);

	//HAL_UART_Transmit(&huart3,(uint8_t *)aRxBuffer,10,0xFFFF);
}
/* USER CODE END 4 */

/* USER CODE BEGIN Header_StartDefaultTask */
/**
  * @brief  Function implementing the defaultTask thread.
  * @param  argument: Not used
  * @retval None
  */
/* USER CODE END Header_StartDefaultTask */
void StartDefaultTask(void *argument)
{
  /* USER CODE BEGIN 5 */
  /* Infinite loop */
	uint8_t ch = 'A';

  for(;;)
  {

	 // HAL_UART_Transmit(&huart3,(uint8_t *)&ch,20,0xFFFF);
	  /*if(ch<'Z')
		  ch++;
	  else ch = 'A';*/

	HAL_GPIO_TogglePin(LED_3_GPIO_Port, LED_3_Pin);
    osDelay(5000);
  }
  /* USER CODE END 5 */
}

/* USER CODE BEGIN Header_motors */
/**
* @brief Function implementing the MotorTask thread.
* @param argument: Not used
* @retval None
*/

/* USER CODE END Header_motors */
//for motor
uint16_t speedA;
uint16_t speedB;
uint16_t readSpeedA;
uint16_t readSpeedB;
bool A_faster = false;
uint16_t dummy = 0;
uint16_t errorVal = 0;

//for sensor var
uint16_t IR1_val;
uint16_t IR2_val;
uint8_t IR1[20];
uint8_t IR2[20];
uint8_t distance1;
uint8_t distance2;
/*uint32_t IC_Val1 = 0;
uint32_t IC_Val2 = 0;
uint32_t Difference = 0;
uint8_t Is_First_Captured = 0;*/
bool check = true;

/*testing for sensor code*/
void sensor()
{
	HAL_ADC_Start(&hadc1);
	HAL_ADC_Start(&hadc2);
	HAL_ADC_PollForConversion(&hadc1, HAL_MAX_DELAY);
	HAL_ADC_PollForConversion(&hadc2, HAL_MAX_DELAY);
	IR1_val = HAL_ADC_GetValue(&hadc1);
	IR2_val = HAL_ADC_GetValue(&hadc2);

	distance2 = (32378.4/IR2_val) - 1.11073;
	distance1 = (30200/IR1_val) + 0.582877;

	sprintf(IR1,"Dist1(cm):%5d\0",distance1);
	OLED_ShowString(10,20,IR1);

	sprintf(IR2,"Dist2(cm):%5d\0",distance2);
	OLED_ShowString(10,30,IR2);

	osDelay(10);
}
/*end of testing for sensor code*/

void stop()
{
	uint16_t speedA = 0;
	uint16_t speedB = 0;
	//uint16_t speedA = 1000;
	//speedA = speedA - offset;
	HAL_GPIO_WritePin(GPIOA, AIN2_Pin, GPIO_PIN_SET);
	HAL_GPIO_WritePin(GPIOA, BIN2_Pin, GPIO_PIN_SET);
	HAL_GPIO_WritePin(GPIOA, AIN1_Pin, GPIO_PIN_RESET);
	HAL_GPIO_WritePin(GPIOA, BIN1_Pin, GPIO_PIN_RESET);
	__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_1, speedA);
	__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_2, speedB);
	osDelay(1);
}
void moveForward()
{
	// for slower spd
	/*speedA = 1200;
	speedB = 750;*/
	//test for faster speed
	speedA = 3500;
	speedB = 3700;
/*	  if (speedA == speedB){
	    dummy += 1;
	  } else if(A_faster){
	    speedA = speedA - errorVal;
	  } else {
	    speedB = speedB - errorVal;
	  }*/
		//uint16_t speedA = 1000;
		//speedA = speedA - offset;
		HAL_GPIO_WritePin(GPIOA, AIN2_Pin, GPIO_PIN_SET);
		HAL_GPIO_WritePin(GPIOA, BIN2_Pin, GPIO_PIN_SET);
		HAL_GPIO_WritePin(GPIOA, AIN1_Pin, GPIO_PIN_RESET);
		HAL_GPIO_WritePin(GPIOA, BIN1_Pin, GPIO_PIN_RESET);
		__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_1, speedA);
		__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_2, speedB);
		osDelay(1);
}
void moveForward1()
{
		//speedA = 800;
		//speedB = 600;
		speedA = 4000;
		speedB = 200;
/*	  if (speedA == speedB){
	    dummy += 1;
	  } else if(A_faster){
	    speedA = speedA - errorVal;
	  } else {
	    speedB = speedB - errorVal;
	  }*/
		//uint16_t speedA = 1000;
		//speedA = speedA - offset;
		HAL_GPIO_WritePin(GPIOA, AIN2_Pin, GPIO_PIN_SET);
		HAL_GPIO_WritePin(GPIOA, BIN2_Pin, GPIO_PIN_SET);
		HAL_GPIO_WritePin(GPIOA, AIN1_Pin, GPIO_PIN_RESET);
		HAL_GPIO_WritePin(GPIOA, BIN1_Pin, GPIO_PIN_RESET);
		__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_1, speedA);
		__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_2, speedB);
		osDelay(1);
}
void moveBackword()
{
	speedA = 3500;
	speedB = 3500;
	  //speedA = 1200;
	  //speedB = 1000;
/*	  if (speedA == speedB){
	    dummy += 1;
	  } else if(A_faster){
	    speedA = speedA - errorVal;
	  } else {
	    speedB = speedB - errorVal;
	  }*/
		//uint16_t speed = 2000;
		//speedA = speedA - offset;
		HAL_GPIO_WritePin(GPIOA, AIN2_Pin, GPIO_PIN_RESET);
		HAL_GPIO_WritePin(GPIOA, AIN1_Pin, GPIO_PIN_SET);
		__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_1, speedA);

		HAL_GPIO_WritePin(GPIOA, BIN2_Pin, GPIO_PIN_RESET);
		HAL_GPIO_WritePin(GPIOA, BIN1_Pin, GPIO_PIN_SET);
		__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_2, speedB);
}
//for turning
void moveBackword1()
{
	speedA = 2200;
	speedB = 1700;
	  if (speedA == speedB){
	    dummy += 1;
	  } else if(A_faster){
	    speedA = speedA - errorVal;
	  } else {
	    speedB = speedB - errorVal;
	  }
		//uint16_t speed = 2000;
		//speedA = speedA - offset;
		HAL_GPIO_WritePin(GPIOA, AIN2_Pin, GPIO_PIN_RESET);
		HAL_GPIO_WritePin(GPIOA, AIN1_Pin, GPIO_PIN_SET);
		__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_1, speedA);

		HAL_GPIO_WritePin(GPIOA, BIN2_Pin, GPIO_PIN_RESET);
		HAL_GPIO_WritePin(GPIOA, BIN1_Pin, GPIO_PIN_SET);
		__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_2, speedB);
}
void turnForward()
{
	htim1.Instance ->CCR4 = 75; // center
}
void turnLeft()
{
	htim1.Instance ->CCR4 = 57; // extreme left

}
void turnRight()
{
	htim1.Instance ->CCR4 = 92; // extreme right

}

void HAL_TIM_IC_CaptureCallback(TIM_HandleTypeDef *htim)
{
	if (htim->Channel == HAL_TIM_ACTIVE_CHANNEL_1)
	{
		if (Is_First_Captured == 0)
		{
			IC_Val1 = HAL_TIM_ReadCapturedValue(htim, TIM_CHANNEL_1);
			Is_First_Captured = 1;
			__HAL_TIM_SET_CAPTUREPOLARITY(htim, TIM_CHANNEL_1, TIM_INPUTCHANNELPOLARITY_FALLING);
		}
		else if (Is_First_Captured == 1)
		{
			IC_Val2 = HAL_TIM_ReadCapturedValue(htim, TIM_CHANNEL_1);
			__HAL_TIM_SET_COUNTER(htim,0);

			if (IC_Val2 > IC_Val1)
			{
				Difference = IC_Val2 - IC_Val1;
			}

			else if (IC_Val1 > IC_Val2)
			{
				Difference = (65535 - IC_Val1) + IC_Val2;
			}

			uDistance = Difference * .034/2;
			//flooruDist = (floor(uDistance / 10) * 10) - 25;
			//flooruDistBack = (floor(uDistance / 10) * 10) - 15;
			//ceiluDist = ceil(uDistance / 10) * 10;

			Is_First_Captured = 0;

			__HAL_TIM_SET_CAPTUREPOLARITY(htim, TIM_CHANNEL_1, TIM_INPUTCHANNELPOLARITY_RISING);
			__HAL_TIM_DISABLE_IT(&htim4, TIM_IT_CC4);
		}
	}
}
void delay(uint16_t time)
{
	__HAL_TIM_SET_COUNTER(&htim4, 0);
	while (__HAL_TIM_GET_COUNTER (&htim4) < time);
}
void HCSR04_Read (void)
{
	HAL_GPIO_WritePin(GPIOD, GPIO_PIN_13, GPIO_PIN_SET);
	delay(10);
	HAL_GPIO_WritePin(GPIOD, GPIO_PIN_13, GPIO_PIN_RESET);
	__HAL_TIM_ENABLE_IT(&htim4, TIM_IT_CC4);
}

sentUART(char sw1)
{
		uint8_t send[4] = " ";
		switch(sw1)
		{
			case 'W':
				sprintf(send,"%s", "PC,W");
				HAL_UART_Transmit(&huart3,(uint8_t *)&send,4,0xFFFF);
				break;
			case 'S':
				sprintf(send,"%s", "PC,S");
				HAL_UART_Transmit(&huart3,(uint8_t *)&send,4,0xFFFF);
				break;
			case 'A':
				sprintf(send,"%s", "PC,A");
				HAL_UART_Transmit(&huart3,(uint8_t *)&send,4,0xFFFF);
				break;
			case 'D':
				sprintf(send,"%s", "PC,D");
				HAL_UART_Transmit(&huart3,(uint8_t *)&send,4,0xFFFF);
				break;
			case 'Q':
				sprintf(send,"%s", "PC,Q");
				HAL_UART_Transmit(&huart3,(uint8_t *)&send,4,0xFFFF);
				break;
			case 'E':
				sprintf(send,"%s", "PC,E");
				HAL_UART_Transmit(&huart3,(uint8_t *)&send,4,0xFFFF);
				break;
			case 'X':
				sprintf(send,"%s", "PC,X");
				HAL_UART_Transmit(&huart3,(uint8_t *)&send,4,0xFFFF);
				break;
			case '0':
				sprintf(send,"%s", "PC,0");
				HAL_UART_Transmit(&huart3,(uint8_t *)&send,4,0xFFFF);
				break;
		}
}

uint8_t inputs(uint16_t udist){
	int distiwant = 40;

	if(checkdis == 1){
		moveForward();
		osDelay(217/8);
		checkdis = 0;
		return 0;
	}

	if(udist <=45){
		stop();
		checkdis =1;
	}
	else{
		moveForward1();
		return 1;
	}
}
void motors(void *argument)
{
  /* USER CODE BEGIN motors */
	//for motor var
	int i = 0;
	int counter=0;
	int dist = 0,total=0;
	HAL_UART_Receive_IT(&huart3,(uint8_t *) aRxBuffer,5);
	HAL_TIM_PWM_Start(&htim8, TIM_CHANNEL_1);
	HAL_TIM_PWM_Start(&htim8, TIM_CHANNEL_2);
	HAL_TIM_PWM_Start(&htim1, TIM_CHANNEL_4);

		for(;;)
		{
			//reset i to 0
			if(i==50)
			{
				i=0;
			}
			switch(aRxBuffer[i])
			{
			//start for test case
				case'1':
					addOsDelay += 5;
					i++;
					break;
				case'2':
					addOsDelay += 10;
					i++;
					break;
				case'3':
					addOsDelay += 50;
					i++;
					break;
				case'4':
					addOsDelay += 100;
					i++;
					break;

					//set forward
				case 'w':
					aRxBuffer[i] = '^';
					if(counter == 5)
					{

						turnRight();
						osDelay(100);
						turnForward();
						counter=0;
					}
					moveForward(770+addOsDelay);
					sentUART('P');
					i++;
					break;
					//set left
				case'q':
					//for turning Left 90
					turnLeft();
					osDelay(500);
					moveForward1();
					osDelay(1850+addOsDelay);
					stop();
					turnForward();
					osDelay(500);
					sentUART('P');
					i++;
					break;
					//set right
				case'e':
					//for turning Right 90
					turnRight();
					osDelay(200);
					moveForward1();
					osDelay(1850+addOsDelay);
					stop();
					turnForward();
					osDelay(500);
					sentUART('P');
					i++;
					//end of test case
				case'Z':
					aRxBuffer[i] = '^';
				//	moveForward();
					while(1)
					{
						sensor();
						if(distance1 <= 30)
						{
							stop();
							sentUART('0');
							break;
						}
					}
					i++;
					break;
				case 'C':
					aRxBuffer[i] = '^';
					moveForward1();
					//60cm
					osDelay(2400);
					i++;
					break;
				case'B':
					aRxBuffer[i] = '^';
					moveForward1();
					//30cm
					osDelay(1200);
					//stop();
					i++;
					break;
					//end for faster car track

				// Move forward in increments
				case 'W':
					//set forward
					aRxBuffer[i] = '^';


					moveForward();
					osDelay(2800);

					stop();

					sentUART('W');
					i++;
					counter++;
					break;

				// Move backwards in increments
				case 'S':
					aRxBuffer[i] = '_';
/*					if(counter == 5)
					{
						turnLeft();
						osDelay(100);
						turnRight();
						osDelay(100);
						turnForward();
						counter=0;
					}*/
					moveBackword(542);
					sentUART('S');
					i++;
					counter++;
					break;

				// On spot turn left
				case'A' :
					aRxBuffer[i] = '>';
					turnLeft();
					osDelay(500);
					moveForward1();
					osDelay(800);
					stop();
					turnRight();
					osDelay(500);
					moveBackword1();
					osDelay(800);
					stop();

					//test
					moveBackword1();
					osDelay(400);
					stop();
					turnLeft();
					osDelay(500);
					moveForward1();
					osDelay(250);
					stop();
					turnForward();
					osDelay(500);
					stop();
					moveForward1();
					osDelay(360);
					stop();

					i++;
					sentUART('A');
					break;

				// On spot turn right
				case 'D':
					aRxBuffer[i] = '>';
					turnRight();
					osDelay(500);
					moveForward1();
					osDelay(800);
					stop();
					turnLeft();
					osDelay(500);
					moveBackword1();
					osDelay(800);
					stop();

					//test
					moveBackword1();
					osDelay(400);
					stop();
					turnRight();
					osDelay(500);
					moveForward1();
					osDelay(290);
					stop();
					turnForward();
					osDelay(500);
					moveForward1();
					osDelay(100);
					stop();

					i++;
					sentUART('D');
					break;

				// Hard turn right
					//set right
				case 'E':
					aRxBuffer[i] = '>';
					//for turning right
					moveForward1();
					osDelay(100);
					stop();
					osDelay(500);

					//for turning Right 90
					turnRight();
					osDelay(200);
					moveForward1();
					osDelay(1950);
					stop();
					turnForward();
					osDelay(500);

					//setMovebackword
					moveBackword1();
					osDelay(350);
					stop();

					sentUART('E');
					i++;
					break;

				// Hard turn left
					//set left
				case 'Q':
					aRxBuffer[i] = '<';
					//for turning left
					moveForward1();
					osDelay(100);
					stop();
					osDelay(500);

					//for turning Left 90
					turnLeft();
					osDelay(500);
					moveForward1();
					osDelay(1850);
					stop();
					turnForward();
					osDelay(500);

					//setMovebackword
					moveBackword1();
					osDelay(400);
					stop();
					sentUART('Q');
					i++;
					break;

				// Stop the robot
				case '0':
					aRxBuffer[i] = '&';
					stop();
					sentUART('0');
					i++;
					break;

				// Center the front wheels
				case 'X':
					aRxBuffer[i] = '-';
					turnForward();
					osDelay(500);
					sentUART('X');
					i++;
					break;

				case 'i'://U-SENSOR
					// full power, 3k pwm, 1000 - 46cm - inside
					// x * 1000/46
					aRxBuffer[i] = '-';
					HCSR04_Read();
/*					while(uDistance == 0){
						HCSR04_Read();
					}*/
					while(1){
						HCSR04_Read();
						sprintf(uSensor, "FDist= %d\n", uDistance);
						OLED_ShowString(10,40,uSensor);
						//delay(200);
						osDelay(200);
						if(uDistance < 40){
							stop();
							break;
						}
						moveForward();
					}
					moveBackword();
					osDelay(534);
					stop();

					//turning start
					//turn left
					turnLeft();
					osDelay(200);
					moveForward();
					osDelay(750);
					//straighten wheel
					turnForward();
					osDelay(500);
					//turn right around obstacle
					turnRight();
					osDelay(200);
					moveForward1();
					osDelay(1800);
					//straighten wheel
					turnForward();
					osDelay(200);
					//move forward
					moveForward();
					osDelay(1300);
					//turn right
					turnRight();
					osDelay(200);
					moveForward1();
					osDelay(1800);
					//straighten wheel
					turnForward();
					osDelay(200);
					moveForward();
					osDelay(100);

					//turn left back to carpark
					osDelay(200);
					turnLeft();
					osDelay(500);
					moveForward();
					osDelay(300);
					//adjust wheel
					turnForward();
					osDelay(200);
					moveBackword();
					osDelay(250);
					stop();

					//back to carpark by sensor
					HCSR04_Read();

					while(1){
						HCSR04_Read();
						sprintf(uSensor, "FDist= %d\n", uDistance);
						OLED_ShowString(10,40,uSensor);
											//delay(200);
						osDelay(200);
						if(uDistance < 30){
							stop();
							break;
						}
						moveForward();
						}
						stop();
					i++;
					break;


				case 'U':
					//move back a bit
					moveBackword();
					osDelay(200);
					//turn left
					turnLeft();
					osDelay(200);
					moveForward();
					osDelay(1100);
					//straighten wheel
					turnForward();
					osDelay(500);
					//turn right around obstacle
					turnRight();
					osDelay(200);
					moveForward();
					osDelay(2800);
					//straighten wheel
					turnForward();
					osDelay(200);
					//move forward
					moveForward();
					osDelay(800);
					//turn right
					turnRight();
					osDelay(200);
					moveForward();
					osDelay(2100);
					//straighten wheel
					turnForward();
					osDelay(500);
					moveForward();
					osDelay(1000);
					//turn left back to carpark
					turnLeft();
					osDelay(200);
					moveForward();
					osDelay(300);
					//adjust wheel
					turnForward();
					osDelay(100);
					moveForward();
					osDelay(500);
					stop();
					i++;
					break;
				case'u':
					aRxBuffer[i] = '-';
					HCSR04_Read();
/*					while(uDistance == 0){
						HCSR04_Read();
					}*/
					while(1){
					HCSR04_Read();
					sprintf(uSensor, "FDist= %d\n", uDistance);
					OLED_ShowString(10,40,uSensor);
					//delay(200);
					osDelay(220);
					HAL_UART_Transmit(&huart3,(uint16_t *)&uSensor,strlen(uSensor),0xFFFF);
					if(uDistance < 40){
						stop();
						break;
					}
					moveForward();
					}
					moveBackword();
					osDelay(434);
					stop();

					uDistance = 0;

					//turning start
					//turn left
					turnLeft();
					osDelay(200);
					moveForward();
					osDelay(700);
					//straighten wheel
/*					turnForward();
					osDelay(500);
					//turn right around obstacle
					turnRight();
					osDelay(200);
					moveForward1();
					osDelay(2200);
					//straighten wheel
					turnForward();
					osDelay(200);
					//move forward
					moveForward();
					osDelay(1800);
					//turn right
					turnRight();
					osDelay(200);
					moveForward1();
					osDelay(1800);
					//straighten wheel
					turnForward();
					osDelay(750);

					//turn left back to carpark
					turnLeft();
					osDelay(200);
					moveForward();
					osDelay(900);
					//adjust wheel
					turnForward();
					osDelay(200);*/
	/*				moveForward();
					osDelay(500);
					stop();

					//back to carpark by sensor
					HCSR04_Read();

					while(1){
						HCSR04_Read();
						sprintf(uSensor, "FDist= %d\n", uDistance);
						OLED_ShowString(10,40,uSensor);
											//delay(200);
						osDelay(200);
						if(uDistance < 40){
							stop();
							break;
						}
						moveForward();
						}*/
						stop();
						i++;
						break;
				case'I':
					aRxBuffer[i] = '-';
					HCSR04_Read();
					while(uDistance == 0){
						HCSR04_Read();
					}
					while(1){
					HCSR04_Read();
					sprintf(uSensor, "FDist= %d\n", uDistance);
					OLED_ShowString(10,40,uSensor);
					delay(200);
					osDelay(100);
					if(uDistance < 150){
						stop();
						break;
					}
					moveForward();
					}
					moveBackword();
					osDelay(834);
					stop();

					uDistance = 0;

					//turning start
					moveBackword();
					osDelay(200);
					//turn left
					turnLeft();
					osDelay(200);
					moveForward();
					osDelay(1100);
					//straighten wheel
					turnForward();
					osDelay(500);
					//turn right around obstacle
					turnRight();
					osDelay(200);
					moveForward();
					osDelay(2800);
					//straighten wheel
					turnForward();
					osDelay(200);
					//move forward
					moveForward();
					osDelay(900);
					//turn right
					turnRight();
					osDelay(200);
					moveForward();
					osDelay(2100);
					//straighten wheel
					turnForward();
					osDelay(500);
					moveForward();
					osDelay(1000);
					//turn left back to carpark
					turnLeft();
					osDelay(200);
					moveForward();
					osDelay(1700);
					//adjust wheel
					turnForward();
					osDelay(100);
					moveForward();
					osDelay(500);
					stop();

					//back to carpark by sensor
					HCSR04_Read();

					while(1){
						HCSR04_Read();
						sprintf(uSensor, "FDist= %d\n", uDistance);
						OLED_ShowString(10,40,uSensor);
											//delay(200);
						osDelay(200);
						if(uDistance < 40){
							stop();
							break;
						}
						moveForward();
						}
						stop();
						i++;
						break;
				case 'g':
						while(1)
						{
							HCSR04_Read();
							sprintf(uSensor, "FDist= %d\n", uDistance);
							OLED_ShowString(10,40,uSensor);
							//delay(200);
							osDelay(350);
						//	HAL_UART_Transmit(&huart3,(uint16_t *)&uSensor,strlen(uSensor),0xFFFF);
						//osDelay(1000);

						}
						i++;
						break;
				/*default:
					while(1)
											{
												HCSR04_Read();
												sprintf(uSensor, "FDist= %d\n", uDistance);
												OLED_ShowString(10,40,uSensor);
											//	delay(200);
												osDelay(220);
												HAL_UART_Transmit(&huart3,(uint16_t *)&uSensor,strlen(uSensor),0xFFFF);
											//osDelay(1000);

											}
											i++;
											break;*/
										//move back a bit

			}osDelay(250);// end for sw case
}//end for "for loop"
  /* USER CODE END motors */
}

/* USER CODE BEGIN Header_show */
/**
* @brief Function implementing the showTask thread.
* @param argument: Not used
* @retval None
*/
/* USER CODE END Header_show */
void show(void *argument)
{
  /* USER CODE BEGIN show */
		uint8_t hello[20] = "hello world\0";
		uint8_t hello1[20] = "hello world\0";

	  	/* Infinite loop */
	  	for(;;)
	  	{
		  sprintf(hello,"%s\0", aRxBuffer);
		  OLED_ShowString(10,40,hello);

		  sprintf(hello1,"%s\0", "MDP-09");
		  OLED_ShowString(10,10,hello1);

		//sprintf(counter,"test:%5d\0",counter);
		//OLED_ShowString(10,30,hello);


		  OLED_Refresh_Gram();
		  osDelay(1000);
	  	}
	  	osDelay(1);
  /* USER CODE END show */
}

/* USER CODE BEGIN Header_encoder_task */
/**
* @brief Function implementing the EncoderTask thread.
* @param argument: Not used
* @retval None
*/
/* USER CODE END Header_encoder_task */
void encoder_task(void *argument)
{
  /* USER CODE BEGIN encoder_task */
		  /* Infinite loop */
		  HAL_TIM_Encoder_Start(&htim2,TIM_CHANNEL_ALL);
		  HAL_TIM_Encoder_Start(&htim3,TIM_CHANNEL_ALL);
		  HAL_TIM_IC_Start_IT(&htim4,TIM_CHANNEL_1);
		  int cnt1,cnt2, cnt3, cnt4;
		  uint32_t tick;
		  uint16_t dir, dir1;

		  cnt1 = __HAL_TIM_GET_COUNTER(&htim2);
		  cnt3 = __HAL_TIM_GET_COUNTER(&htim3);
		  tick = HAL_GetTick();
		  uint8_t SpeedA[20];
		  uint8_t SpeedB[20];
		  for(;;)
		  {
			  if(HAL_GetTick()-tick > 100L){
				  cnt2 = __HAL_TIM_GET_COUNTER(&htim2);
				  cnt4 = __HAL_TIM_GET_COUNTER(&htim3);
				  if(__HAL_TIM_IS_TIM_COUNTING_DOWN(&htim2)){
					  if(cnt2<cnt1)
						  readSpeedA = cnt1 - cnt2;
					  else
						  readSpeedA = (65535 - cnt2) + cnt1;
				  }
				  else{
					  if(cnt2 > cnt1)
						  readSpeedA = cnt2 - cnt1;
					  else
						  readSpeedA = (65535 - cnt1) + cnt2;
				  }
				  if(__HAL_TIM_IS_TIM_COUNTING_DOWN(&htim3)){
					  if(cnt4<cnt3)
						  readSpeedB = cnt3 - cnt4;
					  else
						  readSpeedB = (65535 - cnt4) + cnt3;
				  }
				  else{
					  if(cnt4 > cnt3)
						  readSpeedB = cnt4 - cnt3;
					  else
						  readSpeedB = (65535 - cnt3) + cnt3;
				  }

		      if(readSpeedA > readSpeedB){
		        errorVal = readSpeedA - readSpeedB;
		        A_faster = true;
		      } else{
		        errorVal = readSpeedB - readSpeedA;
		        A_faster = false;
		      }

				  //sprintf(speedA,"Speed:%5d\0",offset);
				  //OLED_ShowString(10,40,offset);
				  //dir = __HAL_TIM_IS_TIM_COUNTING_DOWN(&htim2);
				  //sprintf(hello,"Dir:%5d\0",dir);
				  //OLED_ShowString(10,30,hello);

				  cnt1 = __HAL_TIM_GET_COUNTER(&htim2);
				  cnt3 = __HAL_TIM_GET_COUNTER(&htim3);
				  tick = HAL_GetTick();

			  }
			  osDelay(1);
		  }
  /* USER CODE END encoder_task */
}

/**
  * @brief  This function is executed in case of error occurrence.
  * @retval None
  */
void Error_Handler(void)
{
  /* USER CODE BEGIN Error_Handler_Debug */
  /* User can add his own implementation to report the HAL error return state */
  __disable_irq();
  while (1)
  {
  }
  /* USER CODE END Error_Handler_Debug */
}

#ifdef  USE_FULL_ASSERT
/**
  * @brief  Reports the name of the source file and the source line number
  *         where the assert_param error has occurred.
  * @param  file: pointer to the source file name
  * @param  line: assert_param error line source number
  * @retval None
  */
void assert_failed(uint8_t *file, uint32_t line)
{
  /* USER CODE BEGIN 6 */
  /* User can add his own implementation to report the file name and line number,
     ex: printf("Wrong parameters value: file %s on line %d\r\n", file, line) */
  /* USER CODE END 6 */
}
#endif /* USE_FULL_ASSERT */

/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/
