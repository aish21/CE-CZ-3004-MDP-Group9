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
TIM_HandleTypeDef htim1;
TIM_HandleTypeDef htim2;
TIM_HandleTypeDef htim3;
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
  .stack_size = 128 * 4,
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
void StartDefaultTask(void *argument);
void motors(void *argument);
void show(void *argument);
void encoder_task(void *argument);

/* USER CODE BEGIN PFP */

/* USER CODE END PFP */

/* Private user code ---------------------------------------------------------*/
/* USER CODE BEGIN 0 */
uint8_t aRxBuffer[20];
uint16_t control;
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
  /* USER CODE BEGIN 2 */
  OLED_Init();

  HAL_UART_Receive_IT(&huart3,(uint8_t *) aRxBuffer,5);
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
  __HAL_RCC_GPIOA_CLK_ENABLE();
  __HAL_RCC_GPIOC_CLK_ENABLE();
  __HAL_RCC_GPIOB_CLK_ENABLE();

  /*Configure GPIO pin Output Level */
  HAL_GPIO_WritePin(GPIOE, OLED_SCL_Pin|OLED_SDA_Pin|OLED_RST_Pin|OLED_DC_Pin
                          |LED_3_Pin, GPIO_PIN_RESET);

  /*Configure GPIO pin Output Level */
  HAL_GPIO_WritePin(GPIOA, AIN2_Pin|AIN1_Pin|BIN1_Pin|BIN2_Pin, GPIO_PIN_RESET);

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

}

/* USER CODE BEGIN 4 */
void HAL_UART_RxCpltCallBack(UART_HandleTypeDef *huart)
{
	/* Prevent unused argument(s) compilation warning */
	UNUSED(huart);

	HAL_UART_Transmit(&huart3,(uint8_t *)aRxBuffer,10,0xFFFF);
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
	  HAL_UART_Transmit(&huart3,(uint8_t *)&ch,1,0xFFFF);
	  if(ch<'Z')
		  ch++;
	  else ch = 'A';

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
uint16_t var1;
uint16_t var2;
uint16_t var3;

uint16_t speedA;
uint16_t speedB;
uint16_t readSpeedA;
uint16_t readSpeedB;
bool A_faster = false;
uint16_t dummy = 0;

uint16_t errorVal = 0;
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
void turnR()
{
	htim1.Instance ->CCR4 = 89; // extreme right
	uint16_t speedC = 2500;
	uint16_t speedD = 200;
	HAL_GPIO_WritePin(GPIOA, AIN2_Pin, GPIO_PIN_SET);
	HAL_GPIO_WritePin(GPIOA, BIN2_Pin, GPIO_PIN_SET);
	HAL_GPIO_WritePin(GPIOA, AIN1_Pin, GPIO_PIN_RESET);
	HAL_GPIO_WritePin(GPIOA, BIN1_Pin, GPIO_PIN_RESET);
	__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_1, speedC);
	__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_2, speedD);
	//osDelay(2700); setpoint
	osDelay(3300);
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
	htim1.Instance ->CCR4 = 74; // center
}
void turnL()
{
	//walk str8
	uint16_t speedC = 900;
	uint16_t speedD = 900;
	HAL_GPIO_WritePin(GPIOA, AIN2_Pin, GPIO_PIN_SET);
	HAL_GPIO_WritePin(GPIOA, BIN2_Pin, GPIO_PIN_SET);
	HAL_GPIO_WritePin(GPIOA, AIN1_Pin, GPIO_PIN_RESET);
	HAL_GPIO_WritePin(GPIOA, BIN1_Pin, GPIO_PIN_RESET);
	__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_1, speedC);
	__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_2, speedD);
	osDelay(1500);
	//hard turn left
	speedC = 200;
	speedD = 2300;
	__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_1, speedC);
	__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_2, speedD);
	htim1.Instance ->CCR4 = 57; // extreme left
	osDelay(2200);
	htim1.Instance ->CCR4 = 74; // center
	speedC = 900;
	speedD = 900;
	__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_1, speedC);
	__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_2, speedD);
	osDelay(700);
	uint16_t speedA = 0;
	uint16_t speedB = 0;
	//stop
	//uint16_t speedA = 1000;
	//speedA = speedA - offset;
	HAL_GPIO_WritePin(GPIOA, AIN2_Pin, GPIO_PIN_SET);
	HAL_GPIO_WritePin(GPIOA, BIN2_Pin, GPIO_PIN_SET);
	HAL_GPIO_WritePin(GPIOA, AIN1_Pin, GPIO_PIN_RESET);
	HAL_GPIO_WritePin(GPIOA, BIN1_Pin, GPIO_PIN_RESET);
	__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_1, speedA);
	__HAL_TIM_SetCompare(&htim8, TIM_CHANNEL_2, speedB);

}
void moveForward()
{
		//speedA = 800;
		//speedB = 600;
	speedA = 1200;
	speedB = 690;
	  if (speedA == speedB){
	    dummy += 1;
	  } else if(A_faster){
	    speedA = speedA - errorVal;
	  } else {
	    speedB = speedB - errorVal;
	  }
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
void moveForward1(int a, int b, int c)
{
	/*var1 = atoi(c);
	var2 = atoi(b);
	var3 = atoi(c);*/
		//speedA = 800;
		//speedB = 600;
	speedA = a*100;
	speedB = b*100;
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
	  speedA = 800;
	  speedB = 600;
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
	htim1.Instance ->CCR4 = 74; // center

}
void turnLeft()
{
	htim1.Instance ->CCR4 = 57; // extreme left

}
void turnRight()
{
	htim1.Instance ->CCR4 = 92; // extreme right

}

void motors(void *argument)
{
  /* USER CODE BEGIN motors */
	uint16_t operator[4], i=0,y=0,z=0;
	//uint16_t speedA = 1000;
	//uint16_t speedB = 1000;
	HAL_UART_Receive_IT(&huart3,(uint8_t *) aRxBuffer,5);
	HAL_TIM_PWM_Start(&htim8, TIM_CHANNEL_1);
	HAL_TIM_PWM_Start(&htim8, TIM_CHANNEL_2);
	HAL_TIM_PWM_Start(&htim1, TIM_CHANNEL_4);//testing for front wheel
	//operator[0] = aRxBuffer[0];
	//operator = 0;
	for(;;)
	{
	while(1)
	{
		HAL_UART_Receive_IT(&huart3,(uint8_t *) aRxBuffer,5);
		operator[i] = aRxBuffer[i];
/*		while(operator[0] == '0')
		{
			HAL_UART_Receive_IT(&huart3,(uint8_t *) aRxBuffer,5);
			operator[0] = aRxBuffer[0];
		}*/

		if(i==5)
		{
			i=0;
		}
		switch(operator[i])
		{
			case 'e':
				aRxBuffer[i] = '>';
				operator[i] = '>';
				turnR();
				i++;
				break;
			case 'q':
				aRxBuffer[i] = '<';
				operator[i] = '<';
				turnL();
				i++;
				break;
			case '0':
				aRxBuffer[i] = '&';
				operator[i] = '&';
				stop();
				i++;
				break;
			case 'w':
				aRxBuffer[i] = '^';
				operator[i] = '^';
				moveForward();
				i++;
				break;
			case'a' :
				aRxBuffer[i] = '@';
				operator[i] = '@';
				turnLeft();
				i++;
				break;
			case 'd':
				aRxBuffer[i] = '$';
				operator[i] = '$';
				turnRight();
				i++;
				break;
			case '9':
				aRxBuffer[i] = '-';
				operator[i] = '-';
				turnForward();
				i++;
				break;
			case 's':
				aRxBuffer[i]= '_';
				operator[i]= '_';
				moveBackword();
				i++;
				break;
			case'X':
				while((operator[0] == 'X'))
				{
					HAL_UART_Receive_IT(&huart3,(uint8_t *) aRxBuffer,5);
					operator[1] = aRxBuffer[1];
					operator[2] = aRxBuffer[2] ;
					operator[3] = aRxBuffer[3];
					operator[4] = aRxBuffer[4];
					if(operator[4] == 'X')
					{
						break;
					}
				}

/*				var1 = atoi(operator[1]);
				var2 = atoi(operator[2]);
				var3 = atoi(operator[3]);*/

				switch(operator[1])
				{
					case '1':
						if(operator[2] == '1')
						{
							moveForward();
							operator[0] ='3';
							aRxBuffer[0]='3';
							i--;
							/*operator[4] ='3';
							aRxBuffer[4]='3';*/
							break;
						}
						else if(operator[2] == '2')
						{
							moveBackword();
							i++;
							break;
						}
					case '2':
						if(operator[2] == '1')
						{
							moveBackword();
							i++;
							break;
						}
						else if(operator[2] == '2')
						{
							moveForward();
							i++;
							break;
						}
						break;
				}
				i++;
				break;
	}osDelay(1000);
}
}
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

	  sprintf(hello1,"%s\0", "MDP-GRP9");
	  OLED_ShowString(10,10,hello1);

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
			  sprintf(SpeedA,"Speed:%5d\0",readSpeedA);
			  OLED_ShowString(10,20,SpeedA);
			  sprintf(SpeedB,"Speed:%5d\0",readSpeedB);
			  OLED_ShowString(10,30,SpeedB);

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
